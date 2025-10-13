package com.socialapp.service.admin;

import com.socialapp.model.User;
import com.socialapp.repository.TokenRepository;
import com.socialapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    //!Admin herhangi bir kullanıcıyı silebilir.

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public void deleteUserByAdmin(Long id){
        //!User bilgisi alınır, mevcut değilse hata fırlatılır.
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));

        //!Kullanıcıya ait aktif tokenler ve kullanıcının kendisi silinir.
        tokenRepository.deleteAll(user.getTokens());
        userRepository.delete(user);
    }
}
