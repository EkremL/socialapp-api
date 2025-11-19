package com.socialapp.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.socialapp.dto.auth.PasswordChangeDto;
import com.socialapp.dto.auth.UserDto;
import com.socialapp.dto.auth.UserResponseDto;
import com.socialapp.exception.BadRequestException;
import com.socialapp.exception.ForbiddenException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.exception.UnAuthorizedException;
import com.socialapp.model.*;
import com.socialapp.repository.*;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikePostRepository likePostRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;
    private final TokenRepository tokenRepository;

    //!Id ye göre kullanıcı bilgilerini döndürme
    @Override
    public UserResponseDto getUserById(Long id, String authHeader){
        //?Token üzerinden mevcut kullanıcıyı aldım.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
        //?Hedef Id yi (çağrılmak istenen kişi) databaseden buluyorum.
//        User targetUser = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found!")
//                );
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id)); //?GLOBAL EXCEPTION HANDLER
        //! Eğer target admin ise Forbidden ile unauthorized döndürüyorum yani user admini silemiyor.
        if(targetUser.getRole().name().equals("ADMIN")&& !currentUserProvider.isAdmin(currentUser)){
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Something wrong!");
            throw new ForbiddenException("Something wrong!");
        }

        //! User - UserResponseDto dönüşümü yapıyorum.
        return modelMapper.map(targetUser, UserResponseDto.class);
    }
    //!Şifre güncelleme
    @Override
    public void changePassword(String authHeader, PasswordChangeDto pwDto){
        User user = currentUserProvider.getCurrentUser(authHeader);
        //!Şifreler birbiriyle eşleşiyor mu manual şekilde kontrol ediyorum ve eşleşmiyorlarsa hata fırlatıyorum.
        BCrypt.Result result = BCrypt.verifyer().verify(pwDto.getCurrentPassword().toCharArray(), user.getPassword());

        if(!result.verified){
//            throw new RuntimeException("Passwords are not matching!");
            throw new BadRequestException("Passwords are not matching!");
        }

        if(pwDto.getNewPassword().equals(pwDto.getCurrentPassword())) {
//            throw new RuntimeException("New password cannot be same with old password!");
            throw new BadRequestException("New password cannot be same with old password!");
        }

        //!Yeni şifreyi hashlayıp dbye kaydediyorum.
        String newHashedPw = BCrypt.withDefaults().hashToString(12,pwDto.getNewPassword().toCharArray());


        user.setPassword(newHashedPw);
        userRepository.save(user);
    }
    //! Kullanıcının kendi hesabını silmesi (soft deleteye geçtikten sonra hiyerarşiye uygun biçimde postları commentelri ve likeları da soft delete mantıgında silinmektedir)
    @Override
    public void deleteMyAccount(String authHeader){
        User user = currentUserProvider.getCurrentUser(authHeader);
        if (user == null)
            throw new UnAuthorizedException("User not authenticated!");


        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(user.getId());
        user.setDeletedReason(DeletedReason.USER_SELF_DELETE);
        userRepository.save(user);

        List<Post> posts = postRepository.findEvenIfDeletedByUserId(user.getId());
        posts.forEach(p -> {
            if (!p.isDeleted()) {
                p.setDeleted(true);
                p.setDeletedAt(LocalDateTime.now());
                p.setDeletedBy(user.getId());
                p.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
            }
        });
        postRepository.saveAll(posts);

        List<Comment> comments = commentRepository.findEvenIfDeletedByUserId(user.getId());
        comments.forEach(c -> {

            if(!c.isDeleted()){
                c.setDeleted(true);
                c.setDeletedAt(LocalDateTime.now());
                c.setDeletedBy(user.getId());
                c.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
            }


        });
        commentRepository.saveAll(comments);

        List<LikePost> likes = likePostRepository.findEvenIfDeletedByUserId(user.getId());
        likes.forEach(l -> {
            if(!l.isDeleted()) {
                l.setDeleted(true);
                l.setDeletedAt(LocalDateTime.now());
                l.setDeletedBy(user.getId());
                l.setDeletedReason(DeletedReason.PARENT_USER_DELETE);
            }
        });
        likePostRepository.saveAll(likes);

        tokenRepository.deleteAll(user.getTokens());
    }
    //!(Only for admins) Silinen userları getir.
    @Override
    public List<UserDto> getDeletedUsers(String authHeader) {
        User admin = currentUserProvider.getCurrentUser(authHeader);
        if (!currentUserProvider.isAdmin(admin)) {
            throw new ForbiddenException("Only admins can view deleted users!");
        }

        return userRepository.findDeletedUsers()
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }
    //!(Only for admins) Silinen useri idye göre getir.
    @Override
    public UserDto getDeletedUserById(String authHeader, Long userId) {
        User admin = currentUserProvider.getCurrentUser(authHeader);
        if (!currentUserProvider.isAdmin(admin)) {
            throw new ForbiddenException("Only admins can view deleted users!");
        }

        User user = userRepository.findEvenIfDeleted(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isDeleted()) {
            throw new BadRequestException("This user is not deleted.");
        }

        return modelMapper.map(user, UserDto.class);
    }

    //!(Only for admins) Silinen useri postları, commentleri ve likelarıyla geri getirir! (RESTORE)
    @Override
    public UserDto restoreUser(String authHeader, Long id) {
        User admin = currentUserProvider.getCurrentUser(authHeader);
        if (!currentUserProvider.isAdmin(admin)) {
            throw new ForbiddenException("Only admins can restore users!");
        }

        User user = userRepository.findEvenIfDeleted(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isDeleted()) {
            throw new BadRequestException("User is not deleted!");
        }

        //!User'ı geri getir (restore)
        user.setDeleted(false);
        user.setDeletedAt(null);
        user.setDeletedBy(null);
        user.setDeletedReason(null);
        userRepository.save(user);

        //! User'ın tüm postlarını getir (silinmiş olanlar dahil)
        List<Post> posts = postRepository.findEvenIfDeletedByUserId(user.getId());
        posts.forEach(p -> {
            if (p.isDeleted() && p.getDeletedReason() == DeletedReason.PARENT_USER_DELETE) {
                p.setDeleted(false);
                p.setDeletedAt(null);
                p.setDeletedBy(null);
                p.setDeletedReason(null);
            }
        });
        postRepository.saveAll(posts);

        //! User'ın tüm commentlerini getir (silinmiş olanlar dahil)
        List<Comment> comments = commentRepository.findEvenIfDeletedByUserId(user.getId());
        comments.forEach(c -> {
            if (c.isDeleted() && c.getDeletedReason() == DeletedReason.PARENT_USER_DELETE) {
                c.setDeleted(false);
                c.setDeletedAt(null);
                c.setDeletedBy(null);
                c.setDeletedReason(null);
            }
        });
        commentRepository.saveAll(comments);

        //! User'ın tüm likelarını getir (silinmiş olanlar dahil)
        List<LikePost> likes = likePostRepository.findEvenIfDeletedByUserId(user.getId());
        likes.forEach(l -> {
            if (l.isDeleted() && l.getDeletedReason() == DeletedReason.PARENT_USER_DELETE) {
                l.setDeleted(false);
                l.setDeletedAt(null);
                l.setDeletedBy(null);
                l.setDeletedReason(null);
            }
        });
        likePostRepository.saveAll(likes);

        return modelMapper.map(user, UserDto.class);
    }
}
