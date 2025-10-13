package com.socialapp.repository;

import com.socialapp.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    //! Belirli bir posta ait yorumları user bilgileriyle birlikte getirdim.
    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPostId(Long postId);
}
