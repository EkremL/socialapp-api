package com.socialapp.service.comment;

import com.socialapp.dto.comment.CommentCreateDto;
import com.socialapp.dto.comment.CommentResponseDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.exception.BadRequestException;
import com.socialapp.exception.ForbiddenException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.exception.UnAuthorizedException;
import com.socialapp.model.Comment;
import com.socialapp.model.DeletedReason;
import com.socialapp.model.Post;
import com.socialapp.model.User;
import com.socialapp.repository.CommentRepository;
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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;

    //! Comment entitysini CommentResponseDto'ya dönen sub method.
    private CommentResponseDto newDto(Comment comment){
        //!MODELMAPPER ile temel alanları otomatik mapladım,
        CommentResponseDto dto = modelMapper.map(comment,CommentResponseDto.class);

        //! User kısmını manual mapladım. Çünkü nested mapping otomatik yapılmıyor.
        if(comment.getUser() != null){
            User user = comment.getUser();
            String username= user.getUsername();
            dto.setUserId(user.getId());
            dto.setUsername(username);
        }

        return dto;
    }
    //! Yeni yorum ekleme işlemi
    @Override
    public CommentResponseDto addComment(String authHeader, Long postId, CommentCreateDto commentCreateDto){
        //! Token üzerinden aktif kullanıcıyı ve yoruma ait postu db'den buldum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found!"));

        //! Yeni comment nesnesini oluşturdum ve yorumu dtoya dönüştürerek databaseye kaydettim.
        Comment comment = Comment.builder().text(commentCreateDto.getText()).post(post).user(currentUser).build();
        return newDto(commentRepository.save(comment));
    }
    //! Postun içerisindeki tüm yorumları listeleme işlemi
    @Override
    public List<CommentResponseDto> listAllCommentsInPost(Long postId,String authHeader){
        //!Tüm commentleri veritabanından çekiyorum.
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")){
//            throw new RuntimeException("Login required");
            throw new UnAuthorizedException("Login required");
        }

        currentUserProvider.getCurrentUser(authHeader); //giriş doğrulaması (artık usere atamıyorum doğrudan kontrol sağlıyorum)

        postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found!"));

        return  commentRepository.findByPostId(postId).stream().map(this::newDto).toList();
    }
    //!Yorum silme işlemi
    @Override
    public void deleteComment(String authHeader, Long commentId){
        //!Token üzerinden mevcut kullanıcıyı, daha sonra silinmek istenen yorumu CommentId ye göre buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found!"));

        //!Silme Kontrolü şu şekilde:
        //! 1- Yorum sahibi kendi yorumunu silebilir.
        //! 2- Post sahibi, kendi postundaki herhangi bir yorumu silebilir.
        //! 3- Admin bütün yorumları silebilir.
        //! 4- Post silindiğinde bütün yorumlar otomatik silinir.
        boolean isOwnTheCommentOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isPostOwner = comment.getPost().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUserProvider.isAdmin(currentUser);

        //!Bu durumlar dışında silme işlemi gerçekleştirilemez (bir userin başka bir userin yorumunu silmesi gibi) !
        if(!(isOwnTheCommentOwner || isPostOwner || isAdmin)){
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not authorized to delete comment!");
            throw new ForbiddenException("You are not authorized to delete comment!");}

        //* DELETE REASON
        if (isOwnTheCommentOwner) {
            // User kendi yorumunu siliyor VEYA post sahibi kendi postundaki yorumları siliyor
            comment.setDeletedReason(DeletedReason.USER_SELF_DELETE);
        }
        else if(isPostOwner){
            comment.setDeletedReason(DeletedReason.PARENT_POST_DELETE);
        }
        else{
            // Admin siliyor
            comment.setDeletedReason(DeletedReason.ADMIN_DELETE);
        }

//        //! Authorization kontrolünden sonra yorumu siliyorum.
//        commentRepository.delete(comment); (!ESKİ (SOFT DELETE ÖNCESİ)
        //!SOFT DELETE
        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        comment.setDeletedBy(currentUser.getId());
        commentRepository.save(comment);
    }
    //!(Only for admins) Silinen tüm commentleri getirir
    @Override
    public List<CommentResponseDto> getDeletedComments(String authHeader) {
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if (!currentUserProvider.isAdmin(currentUser)) {
            throw new ForbiddenException("Only admins can view deleted comments!");
        }

        return commentRepository.findDeletedComments()
                .stream()
                .map(c -> CommentResponseDto.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .userId(c.getUser() != null ? c.getUser().getId() : null)
                        .username(c.getUser() != null ? c.getUser().getUsername() : null)
                        .postId(c.getPost() != null ? c.getPost().getId() : null) // sadece id
                        .createdAt(c.getCreatedAt())
                        .build()
                )
                .toList();
    }
    //! (Only for admins)Silinen commenti idye göre getir
    @Override
    public CommentResponseDto getDeletedCommentById(String authHeader, Long commentId) {
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if (!currentUserProvider.isAdmin(currentUser)) {
            throw new ForbiddenException("Only admins can view deleted comment!");
        }

        Comment comment = commentRepository.findEvenIfDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found!"));

        if (!comment.isDeleted()) {
            throw new BadRequestException("This comment is not deleted.");
        }

        return newDto(comment);
    }
    //! (Only for admins)Commentleri Restore etme işlemi (silinmiş olanlar dahil)
    @Override
    public CommentResponseDto restoreComment(String authHeader, Long commentId) {
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if (!currentUserProvider.isAdmin(currentUser)) {
            throw new ForbiddenException("Only admins can restore comments!");
        }

        Comment comment = commentRepository.findEvenIfDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found!"));

        if (!comment.isDeleted()) {
            throw new BadRequestException("Comment is not deleted.");
        }
        // Parent User silikse restore edilemez
        if (comment.getUser() != null && comment.getUser().isDeleted()) {
            throw new BadRequestException("User is deleted. Restore the user first.");
        }

        // Parent Post silikse hata
        if (comment.getPost().isDeleted()) {
            throw new BadRequestException("Post is deleted. Restore the post first.");
        }

            comment.setDeleted(false);
            comment.setDeletedAt(null);
            comment.setDeletedBy(null);
            comment.setDeletedReason(null);
            commentRepository.save(comment);

            return newDto(comment);
    }
}
