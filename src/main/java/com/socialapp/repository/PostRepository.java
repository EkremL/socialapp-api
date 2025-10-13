package com.socialapp.repository;

import com.socialapp.model.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository  extends JpaRepository<Post,Long> {
    //! Tüm postları kullanıcı ve commentleriyle birlikte çağırmak için kullandım.
    @EntityGraph(attributePaths = {"user","comments","comments.user"})
    List<Post> findAll();

    //! Tek bir postu user bilgisiyle birlikte getiriyor.
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findById(Long id);
}
