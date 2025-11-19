package com.socialapp.service.post;

import com.socialapp.dto.comment.CommentResponseDto;
import com.socialapp.dto.post.PostCreateDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.dto.post.PostUpdateDto;
import com.socialapp.exception.BadRequestException;
import com.socialapp.exception.ForbiddenException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.model.*;
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.LikePostRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikePostRepository likePostRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;

    private boolean isOwnThePost(User user, Post post){
        return post.getUser().getId().equals(user.getId());
    }
    //!Post entitysini postresponsedto'ya dönüştüren sub method
    private PostResponseDto newDto(Post post){
    //! Manual mappingin bir kısmını modelmapper ile otomatiğe çevirdim, nested user ve comment list'i manual set ettim.
        PostResponseDto dto = modelMapper.map(post, PostResponseDto.class);

        if (post.getUser() != null){
            dto.setUserId(post.getUser().getId());
            dto.setUsername(post.getUser().getUsername());
        }
        //!bu işlem eğer posta ait yorum varsa yapılıyor, her yorumu dtoya dönüştürüp ekliyorum. (Postlar fetch edildiğinde user ve commentlerle gelsin diye yaptım)
        if(post.getComments() != null && !post.getComments().isEmpty()){
            dto.setComments(post.getComments().stream().
                    map(comment -> {CommentResponseDto commentDto =  modelMapper.map(comment, CommentResponseDto.class);
                    if(comment.getUser() != null){
                    commentDto.setUsername(comment.getUser().getUsername());
                    commentDto.setUserId(comment.getUser().getId());
                    }
                    return  commentDto;
                    }).toList());
        }
        return dto;
    }

    //!Yeni post oluşturma işlemi
    @Override
    public PostResponseDto createPost(String authHeader, PostCreateDto postCreateDto){
        //!Token üzerinden mevcut kullanıcıyı alıp yeni post entitysini oluşturuyorum ve sub method aracılığıyla dtyoya dönüştürüp kaydediyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
        Post post = Post.builder()
                .user(currentUser)
                .imageUrl(postCreateDto.getImageUrl())
                .description(postCreateDto.getDescription())
                .build();

        return newDto(postRepository.save(post));
    }
    //!Tekli postu döndürme
    @Override public PostResponseDto getPostById(Long id, String authHeader){
        currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundException("Post not found!"));
        return newDto(post);
    }
    //!Tüm postları listeleme
    @Override
    public List<PostResponseDto> getAllPosts(String authHeader){
        currentUserProvider.getCurrentUser(authHeader);
        return postRepository.findAll().stream().map(this::newDto).toList();
        //.map(post -> newDto(post)) da olurdu
        //collect(Collectors.toList()) de olurdu ama yeni javalarda (16+) toList yeterli
    }
    //!Post güncelleme işlemi
    @Override
    public PostResponseDto updatePost(String authHeader, PostUpdateDto postUpdateDto, Long id){
        //!Token üzerinden mevcut kullanıcıyı, daha sonra postu idsine göre buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundException("Post not found!"));

        //!Yalnızca post sahibi veya admin güncelleme yapabilir.
        if(!(isOwnThePost(currentUser,post) || currentUserProvider.isAdmin(currentUser))) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the post owner or admin can update!");
            throw new ForbiddenException("Only the post owner or admin can update!");
        }
        //!Update edilen alanları post'a set ediyorum.
        if(postUpdateDto.getImageUrl() != null)
            post.setImageUrl(postUpdateDto.getImageUrl());
        if(postUpdateDto.getDescription() != null)
            post.setDescription(postUpdateDto.getDescription());

        //?Güncellenmiş postu kaydedip DTO olarak döndürdüm.
        return newDto(postRepository.save(post));
    }
    //!Post silme işlemi
    @Override
    public void deletePost(String authHeader, Long id){
        //!Token üzerinden mevcut kullanıcıyı, daha sonra postu idsine göre buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundException("Post not found!"));

        //!Yalnızca post sahibi veya admin silme yapabilir.
        if(!(isOwnThePost(currentUser,post) || currentUserProvider.isAdmin(currentUser))){
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the post owner or admin can update!");
            throw new ForbiddenException("Only the post owner or admin can delete!");}

        //!DELETE REASON
        if (isOwnThePost(currentUser, post)) {
            // Kullanıcı kendi postunu siliyor
            post.setDeletedReason(DeletedReason.USER_SELF_DELETE);
        } else if (currentUserProvider.isAdmin(currentUser)) {
            // Admin siliyor
            post.setDeletedReason(DeletedReason.ADMIN_DELETE);
        }

        //*SOFT DELETE
        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());
        post.setDeletedBy(currentUser.getId());

        postRepository.save(post); // SOFT DELETE — entity flag set + save

        //*Soft delete comments of this post
        List<Comment> comments = commentRepository.findEvenIfDeletedByPostId(post.getId());
        comments.forEach(c -> {
            if(!c.isDeleted()){
                c.setDeleted(true);
                c.setDeletedAt(LocalDateTime.now());
                c.setDeletedBy(currentUser.getId());
                c.setDeletedReason(DeletedReason.PARENT_POST_DELETE);
            }
        });
        commentRepository.saveAll(comments);

        //*Soft delete likes of this post
        List<LikePost> likes = likePostRepository.findEvenIfDeletedByPostId(post.getId());
        likes.forEach(l -> {
            if(!l.isDeleted()){
                l.setDeleted(true);
                l.setDeletedAt(LocalDateTime.now());
                l.setDeletedBy(currentUser.getId());
                l.setDeletedReason(DeletedReason.PARENT_POST_DELETE);
            }
        });
        likePostRepository.saveAll(likes);
    }
    //! Increment View Count
    @Override
    public void incrementViewCount(Long id, String authHeader){

//        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundException("Post not found!"));
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(post.getUser().getId().equals(currentUser.getId()) && post.getViewCount() > 0){
            return;
        } //!manüpilasyonu (kendi postuna baktığında viewcount arttırmayı) engellemek için yazdım

        //!Viewcount + 1 ile arttırdım ve dbde güncelledim.
        post.setViewCount(post.getViewCount() + 1);

        postRepository.save(post);
    }
    //! Get Deleted Posts (Only for admins) (after soft delete)
    @Override
    public List<PostResponseDto> getDeletedPosts(String authHeader){
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(!currentUserProvider.isAdmin(currentUser)){
            throw new ForbiddenException("Only admins can view deleted posts!");
        }

        return postRepository.findDeletedPosts()
                .stream()
                .map(this::newDto)
                .toList();
    }
    //! Get Deleted Post By Id (Only for admins) (after soft delete)
    public PostResponseDto getDeletedPostById(String authHeader, Long id) {
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(!currentUserProvider.isAdmin(currentUser)){
            throw new ForbiddenException("Only admins can view a deleted post!");
        }

        Post post = postRepository.findEvenIfDeleted(id)
                .orElseThrow(() -> new NotFoundException("Post not found!"));

        if(!post.isDeleted()) {
            throw new BadRequestException("This post is not deleted.");
        }

        return newDto(post);
    }

    //! Restore Deleted Post By Id (Only for admins) (after soft delete)
    @Override
    public PostResponseDto restorePost(String authHeader, Long id){
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(!currentUserProvider.isAdmin(currentUser)){
            throw new ForbiddenException("Only admins can restore posts!");
        }

        Post post = postRepository.findEvenIfDeleted(id)
                .orElseThrow(() -> new NotFoundException("Post not found!"));

        if (post.getUser() != null && post.getUser().isDeleted()) {
            throw new BadRequestException("Owner user is deleted. Restore the user first.");
        }


        post.setDeleted(false);
        post.setDeletedAt(null);
        post.setDeletedBy(null);
        postRepository.save(post);


        List<Comment> comments = commentRepository.findEvenIfDeletedByPostId(post.getId());
        comments.forEach(c -> {
            if (c.isDeleted() && c.getDeletedReason() == DeletedReason.PARENT_POST_DELETE) {
                c.setDeleted(false);
                c.setDeletedAt(null);
                c.setDeletedBy(null);
                c.setDeletedReason(null);
            }
        });
        commentRepository.saveAll(comments);


        List<LikePost> likes = likePostRepository.findEvenIfDeletedByPostId(post.getId());
        likes.forEach(l -> {
            if (l.isDeleted() && l.getDeletedReason() == DeletedReason.PARENT_POST_DELETE) {
                l.setDeleted(false);
                l.setDeletedAt(null);
                l.setDeletedBy(null);
                l.setDeletedReason(null);
            }
        });
        likePostRepository.saveAll(likes);

        return newDto(post);
    }

}
