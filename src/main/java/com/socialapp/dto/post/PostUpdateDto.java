package com.socialapp.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDto {
    //!Post güncellemek icin kullandığım DTO.
    @NotBlank(message = "Image URL is required!")
    private String imageUrl;
    @NotBlank(message = "Description is required!")
    private String description;
}
