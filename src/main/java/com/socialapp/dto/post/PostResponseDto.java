package com.socialapp.dto.post;

import com.socialapp.dto.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
    //!Post verilerini döndürmek için oluşturduğum DTO.
    //!PostServiceImpl içinde model maplama yaparken kullandım.
    private Long id;
    private String imageUrl;
    private String description;
    private int viewCount;
    private int likeCount;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments;
}
