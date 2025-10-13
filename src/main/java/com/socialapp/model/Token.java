package com.socialapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //!üretilen access token
    private String token;

    private String username;

    private boolean expired;

    private boolean revoked;

    //!Token oluşunca otomatik timestamp setleniyor.
    @Column(name = "created_at",updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    //!Token hangi kullanıcıya ait gösterilmekte.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
