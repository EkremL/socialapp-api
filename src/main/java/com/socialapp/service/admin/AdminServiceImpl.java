package com.socialapp.service.admin;

import com.socialapp.exception.ForbiddenException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.model.*;
import com.socialapp.repository.*;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    //!Admin herhangi bir kullanıcıyı silebilir.

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;
    private final CommentRepository commentRepository;
    private final TokenRepository tokenRepository;
    private  final CurrentUserProvider currentUserProvider;

@Override
public void deleteUserByAdmin(Long id, String authHeader) {


    User admin = currentUserProvider.getCurrentUser(authHeader);
    if (!currentUserProvider.isAdmin(admin)) {
        throw new ForbiddenException("You are not authorized to delete users!");
    }

    User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));


    if (user.getId().equals(admin.getId())) {
        throw new ForbiddenException("Admin cannot delete themselves!");
    }


    user.setDeleted(true);
    user.setDeletedAt(LocalDateTime.now());
    user.setDeletedBy(admin.getId());
    user.setDeletedReason(DeletedReason.ADMIN_DELETE);
    userRepository.save(user);


    List<Post> posts = postRepository.findEvenIfDeletedByUserId(user.getId());
    posts.forEach(p -> {
        if (!p.isDeleted()) {
            p.setDeleted(true);
            p.setDeletedAt(LocalDateTime.now());
            p.setDeletedBy(admin.getId());
            p.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
        }
    });
    postRepository.saveAll(posts);


    List<Comment> comments = commentRepository.findEvenIfDeletedByUserId(user.getId());
    comments.forEach(c -> {
        if (!c.isDeleted()) {
            c.setDeleted(true);
            c.setDeletedAt(LocalDateTime.now());
            c.setDeletedBy(admin.getId());
            c.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
        }
    });
    commentRepository.saveAll(comments);


    List<LikePost> likes = likePostRepository.findEvenIfDeletedByUserId(user.getId());
    likes.forEach(l -> {
        if (!l.isDeleted()) {
            l.setDeleted(true);
            l.setDeletedAt(LocalDateTime.now());
            l.setDeletedBy(admin.getId());
            l.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
        }
    });
    likePostRepository.saveAll(likes);


    tokenRepository.deleteAll(user.getTokens());
}

}
