package com.socialapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
public class Comment extends SoftDeletable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    //! Her yorumun bir posta bağlı olmasını sağladım.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId",nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comments", "likes"})
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
