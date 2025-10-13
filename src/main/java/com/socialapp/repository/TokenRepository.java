package com.socialapp.repository;

import com.socialapp.model.Token;
import com.socialapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    //? Token doğrulama ve logout işlemlerinde kullandım.
    Optional<Token> findByToken(String token);
}
