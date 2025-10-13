package com.socialapp.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreateDto {
    //!Comment oluştururken bu dtoyu kullandım.
    @NotBlank(message = "Comment text is required!")
    private String text;
}
