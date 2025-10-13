package com.socialapp.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    //!Yorum oluşturma ve listeleme işlemleri için bu veri modelini tasarladım. Ayrıca CommentServiceImpl  içerisinde maplama ile kullandım.
    private Long id;
    private String text;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
}
