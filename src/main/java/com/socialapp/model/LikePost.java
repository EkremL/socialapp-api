package com.socialapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
@Table(name = "post_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"postId","userId"} ))
public class LikePost extends SoftDeletable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //! Hangi posta like atıldığı tutuluyor.
    @ManyToOne(optional = false)
    @JoinColumn(name = "postId")
    private Post post;

    //! Like atan kullanıcının bilgisini tuttum.
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private User user;

}
