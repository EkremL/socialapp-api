package com.socialapp.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.socialapp.dto.auth.PasswordChangeDto;
import com.socialapp.dto.auth.UserResponseDto;
import com.socialapp.model.User;
import com.socialapp.repository.TokenRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;
    private final TokenRepository tokenRepository;

    //!Id ye göre kullanıcı bilgilerini döndürme
    @Override
    public UserResponseDto getUserById(Long id, String authHeader){
        //?Token üzerinden mevcut kullanıcıyı aldım.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
        //?Hedef Id yi (çağrılmak istenen kişi) databaseden buluyorum.
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!")
                );
        //! Eğer target admin ise Forbidden ile unauthorized döndürüyorum yani user admini silemiyor.
        if(targetUser.getRole().name().equals("ADMIN")&& !currentUserProvider.isAdmin(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Something wrong!");

        //! User - UserResponseDto dönüşümü yapıyorum.
        return modelMapper.map(targetUser, UserResponseDto.class);
    }
    //!Şifre güncelleme
    @Override
    public void changePassword(String authHeader, PasswordChangeDto pwDto){
        User user = currentUserProvider.getCurrentUser(authHeader);
        //!Şifreler birbiriyle eşleşiyor mu manual şekilde kontrol ediyorum ve eşleşmiyorlarsa hata fırlatıyorum.
        BCrypt.Result result = BCrypt.verifyer().verify(pwDto.getCurrentPassword().toCharArray(), user.getPassword());

        if(!result.verified)
            throw new RuntimeException("Passwords are not matching!");

        if(pwDto.getNewPassword().equals(pwDto.getCurrentPassword()))
            throw new RuntimeException("New password cannot be same with old password!");

        //!Yeni şifreyi hashlayıp dbye kaydediyorum.
        String newHashedPw = BCrypt.withDefaults().hashToString(12,pwDto.getNewPassword().toCharArray());


        user.setPassword(newHashedPw);
        userRepository.save(user);
    }
    //! Kullanıcının kendi hesabını silmesi
    @Override
    public void deleteMyAccount(String authHeader){
        User user = currentUserProvider.getCurrentUser(authHeader);
        //!Kullanıcıya ait tüm tokenler, daha sonra hesap siliniyor.
        tokenRepository.deleteAll(user.getTokens());

        userRepository.delete(user);
    }

}
