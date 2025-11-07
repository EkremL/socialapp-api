package com.socialapp.service.comment;

import com.socialapp.dto.comment.CommentCreateDto;
import com.socialapp.dto.comment.CommentResponseDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.exception.ForbiddenException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.exception.UnAuthorizedException;
import com.socialapp.model.Comment;
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

        //! Authorization kontrolünden sonra yorumu siliyorum.
        commentRepository.delete(comment);
    }



}
