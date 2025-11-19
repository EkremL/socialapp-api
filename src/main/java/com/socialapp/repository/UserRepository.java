package com.socialapp.repository;

import com.socialapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//! DotNet'teki DbContext mantığındaki Repository'i oluşturup CRUD methodlarına erişim sağladım.
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //? Login işleminde kullanıcıyı username üzerinden çağırmak için kullandım.
    Optional<User> findByUsername(String username);

    //? Veritabanında aynı isimde veya emailde birisi varsa kayıt yapılmaması için kontrol sağladım.
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE is_deleted = true", nativeQuery = true)
    List<User> findDeletedUsers();

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<User> findEvenIfDeleted(@Param("id") Long id);
}
