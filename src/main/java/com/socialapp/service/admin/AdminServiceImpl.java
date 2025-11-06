package com.socialapp.service.admin;

import com.socialapp.model.User;
import com.socialapp.repository.TokenRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    //!Admin herhangi bir kullanıcıyı silebilir.

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private  final CurrentUserProvider currentUserProvider;

    @Override
    public void deleteUserByAdmin(Long id, String authHeader){
        //!Target User bilgisi alınır, mevcut değilse hata fırlatılır.
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(!currentUserProvider.isAdmin(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete anyone!");


        //!Kullanıcıya ait aktif tokenler ve kullanıcının kendisi silinir.
        tokenRepository.deleteAll(user.getTokens());
        userRepository.delete(user);
    }
}
