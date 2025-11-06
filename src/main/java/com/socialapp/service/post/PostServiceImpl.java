package com.socialapp.service.post;

import com.socialapp.dto.comment.CommentResponseDto;
import com.socialapp.dto.post.PostCreateDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.dto.post.PostUpdateDto;
import com.socialapp.model.Comment;
import com.socialapp.model.Post;
import com.socialapp.model.User;
import com.socialapp.repository.PostRepository;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
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
    @Override public PostResponseDto getPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        return newDto(post);
    }
    //!Tüm postları listeleme
    @Override
    public List<PostResponseDto> getAllPosts(){
        return postRepository.findAll().stream().map(this::newDto).toList();
        //.map(post -> newDto(post)) da olurdu
        //collect(Collectors.toList()) de olurdu ama yeni javalarda (16+) toList yeterli
    }
    //!Post güncelleme işlemi
    @Override
    public PostResponseDto updatePost(String authHeader, PostUpdateDto postUpdateDto, Long id){
        //!Token üzerinden mevcut kullanıcıyı, daha sonra postu idsine göre buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));

        //!Yalnızca post sahibi veya admin güncelleme yapabilir.
        if(!(isOwnThePost(currentUser,post) || currentUserProvider.isAdmin(currentUser)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the post owner or admin can update!");
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
        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));

        //!Yalnızca post sahibi veya admin silme yapabilir.
        if(!(isOwnThePost(currentUser,post) || currentUserProvider.isAdmin(currentUser)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the post owner or admin can update!");

       postRepository.delete(post);
    }

    @Override
    public void incrementViewCount(Long id, String authHeader){

        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found!"));
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(post.getUser().getId().equals(currentUser.getId()) && post.getViewCount() > 0){
            return;
        } //!manüpilasyonu (kendi postuna baktığında viewcount arttırmayı) engellemek için yazdım

        //!Viewcount + 1 ile arttırdım ve dbde güncelledim.
        post.setViewCount(post.getViewCount() + 1);

        postRepository.save(post);
    }

}
