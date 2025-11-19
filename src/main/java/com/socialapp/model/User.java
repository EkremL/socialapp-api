package com.socialapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
public class User extends SoftDeletable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //!Login işlemleri username ve pw üzerinden olacak, emaili sonradan ekledim. Profil sayfasında /me ile göstermek için.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) //! Rolleri Db'de 0,1 yerine Admin,User şeklinde String olarak kaydedilmesini sağladım.
    private Role role;

    //! Ek olarak timestamp ekledim, böylece user ilk kez kayıt oldugunda oluşur ve bir daha değiştirilemez.
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
    //!RELATIONS
    //?Kullanıcı ile token arasında bire çok (one to many) ilişki oluşturarak kullanıcı silinince tokenlerinin de silinmesini sağladım.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    //?Kullanıcının birden fazla postu olabilir ve kullanıcı silinirse tüm postlar silinir.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    //?Kullanıcı birden fazla yorum yapabilir, yorumlar silinince postları etkilemez.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    //? Kullanıcı birden fazla posta like atabilir.
    //? Post silinince like'lar da silinir. Comment ile şimdilik ilişkisi yok.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikePost> likes = new ArrayList<>();
}
