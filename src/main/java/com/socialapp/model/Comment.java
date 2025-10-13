package com.socialapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    //! Her yorumun bir posta bağlı olmasını sağladım.
    @ManyToOne(optional = false)
    @JoinColumn(name = "postId",nullable = false)
    private Post post;

    //! Her yorumun bir usere ait olmasını sağladım.
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId",nullable = false)
    private User user;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
