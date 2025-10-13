package com.socialapp.repository;

import com.socialapp.model.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost,Long>{

    //!Aynı kullanıcı aynı postu tekrar beğenmesin diye kontrol sağladım.
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    //! Unlike işleminde ilgili kaydın bulunmasını sağladım.
    Optional<LikePost> findByPostIdAndUserId(Long postId,Long userId);

    //! Toplam beğeni sayısını post üzerinde güncelledim. Yani like işleminde önceki beğenileri alıp üstüne 1 ekleyip likeCount'un kaydedilmesini sağladım.
    long countByPostId(Long postId);
}
