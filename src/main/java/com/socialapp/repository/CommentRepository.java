package com.socialapp.repository;

import com.socialapp.model.Comment;
import com.socialapp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    //! Belirli bir posta ait yorumları user bilgileriyle birlikte getirdim.
    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPostId(Long postId);

    @Query(value = "SELECT * FROM comment WHERE is_deleted = true", nativeQuery = true)
    List<Comment> findDeletedComments();

    @Query(value = "SELECT * FROM comment WHERE id = :id", nativeQuery = true)
    Optional<Comment> findEvenIfDeleted(@Param("id") Long id);

    // Bir postun tüm comment'leri (silinmiş dahil)
// NEDEN? Post silinince/restore edilince, child comment'leri zincirde yönetmek için.
    @Query(value = "SELECT * FROM comment WHERE post_id = :postId", nativeQuery = true)
    List<Comment> findEvenIfDeletedByPostId(@Param("postId") Long postId);

    // (Opsiyonel) Kullanıcının tüm comment'leri (silinmiş dahil)
// NEDEN? User silinince, doğrudan user → comment zincirini kurmak istersen.
    @Query(value = "SELECT * FROM comment WHERE user_id = :userId", nativeQuery = true)
    List<Comment> findEvenIfDeletedByUserId(@Param("userId") Long userId);

}
