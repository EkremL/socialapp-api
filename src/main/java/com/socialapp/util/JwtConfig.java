package com.socialapp.util;


import com.socialapp.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtConfig {

    //! Localde "openssl rand -base64 32" ile 256-bit güvenli key ürettim ve veri güvenliği açısından .env klasörü içerisinde tanımladım.
    @Value("${SECRET_KEY}")
    private String jwtSecret;

    //! 256 bit key, byte dizisine çevriliyor ve token imzası için HS256 algoritması için uyumlu hale geliyor.
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    //!Jwt token oluşturma.
    //?Username ve claimler dahil edilerek token oluşturuluyor.
    public String generateToken(String username, Map<String,Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME)) //!Constants üzerinde tanımladığım 30 günlük expiration'ı burada çağırdım.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) //! HS256 token imzası
                .compact();
    }
    //! Alttaki 2 method sırasıyla şu işlemleri yapmakta:
    //! Token içerisindeki tüm claimleri alma
    //! Token süresini dönme (doldu mu dolmadı mı)
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build().
                parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

}
