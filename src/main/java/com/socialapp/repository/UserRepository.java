package com.socialapp.repository;

import com.socialapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//! DotNet'teki DbContext mantığındaki Repository'i oluşturup CRUD methodlarına erişim sağladım.
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //? Login işleminde kullanıcıyı username üzerinden çağırmak için kullandım.
    Optional<User> findByUsername(String username);

    //? Veritabanında aynı isimde veya emailde birisi varsa kayıt yapılmaması için kontrol sağladım.
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
