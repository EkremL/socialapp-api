package com.socialapp.util;

import com.socialapp.exception.NotFoundException;
import com.socialapp.exception.UnAuthorizedException;
import com.socialapp.model.Token;
import com.socialapp.model.User;
import com.socialapp.repository.TokenRepository;
import com.socialapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor


public class CurrentUserProvider {

    private final TokenRepository tokenRepository;
    //! Bu method sayesinde Authorization header üzerinden mevcut kullanıcı tespit ediliyor.
    //? Her requestte headerdeki token kontrol edilip hala geçerli mi değil mi kontrol ediliyor.
    //? Eğer token geçerliyse, ilişkili kullanıcı nesnesini döndürüyorum.
    public User getCurrentUser(String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
//            throw new RuntimeException("Authorization header isn't found or invalid!");
            throw new UnAuthorizedException("Authorization header isn't found or invalid!");
        }

        String token = authHeader.substring(7);
        //! "Bearer " kısmının atılarak yalnızca tokenin alınmasını sagladım.
        //! Aslında Bearer_Prefix gibi bir constants tanımlayarak "Bearer " kısmını constant hale getirebilirdim fakat fazla soyutlama ve testlerde sıkıntı çıkabileceğini düşünerek bu yöntemi kullandım.

        //!Tokeni dbde buluyorum, yoksa hata fırlatıyorum. Eğer süresi dolmuş veya geçersizse işleme engel oluyorum.
//        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token isn't found"));
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new UnAuthorizedException("Token isn't found"));
        if(savedToken.isExpired() || savedToken.isRevoked()){
//            throw new RuntimeException("Token is expired or revoked!");
            throw new UnAuthorizedException("Token is expired or revoked!");
        }

        //!Kontrollerden sonra tokene bağlı useri döndürüyorum.
        return savedToken.getUser();

    }
    //? Bu yardımcı methodu ise korumalı endpointlerde kod tekrarına düşmemek için yazdım. Böylece admin ile ilgili (örneğin adminin kullanıcıları silebilmesi ama kullanıcıların başkalarını silememesi gibi) endpointlerde koruma sağlanmış oldu.
    public boolean isAdmin(User user){
        return user.getRole().name().equals("ADMIN");
    }
}
