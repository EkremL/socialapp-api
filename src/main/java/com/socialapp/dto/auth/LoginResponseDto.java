package com.socialapp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    //!Giriş yapıldıktan sonra token oluşturuluyor ve bu token, geçerlilik süresiyle birlikte databaseye kaydediliyor.
    private String token;
    private String expiresAt;
}
