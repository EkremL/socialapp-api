package com.socialapp.repository;

import com.socialapp.model.LikePost;
import com.socialapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost,Long>{

    //!Aynı kullanıcı aynı postu tekrar beğenmesin diye kontrol sağladım.
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    //! Unlike işleminde ilgili kaydın bulunmasını sağladım.
    Optional<LikePost> findByPostIdAndUserId(Long postId,Long userId);

    //! Toplam beğeni sayısını post üzerinde güncelledim. Yani like işleminde önceki beğenileri alıp üstüne 1 ekleyip likeCount'un kaydedilmesini sağladım.
    long countByPostId(Long postId);

    // Bir postun tüm like'ları (silinmiş dahil)
// NEDEN? Post silinince/restore edilince, child like'ları zincirde yönetmek için.
    @Query(value = "SELECT * FROM post_likes WHERE post_id = :postId", nativeQuery = true)
    List<LikePost> findEvenIfDeletedByPostId(@Param("postId") Long postId);

    // (Opsiyonel) Kullanıcının tüm like'ları (silinmiş dahil)
// NEDEN? User silinince, doğrudan user → like zincirini kurmak istersen.
    @Query(value = "SELECT * FROM post_likes WHERE user_id = :userId", nativeQuery = true)
    List<LikePost> findEvenIfDeletedByUserId(@Param("userId") Long userId);

}
