package com.socialapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 2500, nullable = false)
    private String description;

    private int viewCount = 0;
    private int likeCount = 0;

    //!Birden fazla post tek kullanıcıya bağlı olabilir.
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    //!Post silindiğinde ona bağlı tüm yorumların silinmesini sağladım.
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    //!Aynı şekilde post silinince bütün beğeniler de siliniyor.
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikePost> likes = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    //!2 adet prepersist kullanınca error aldım bu yöntemi tercih ettim.
    @PrePersist void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
