package com.socialapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//! Kullanıcıya ait ve yalnızca gerekli bilgileri taşıyarak ve gerekli validasyonları sağlayarak veritabanını soyutlayan bir dto classı yazdım.
//! Register işlemi için de bu dtoyı kullandım.
public class UserDto {
    @NotBlank(message = "Username is required!")
    @Size(min = 2, max = 20, message = "Username must be 2-20 characters.")
    private String username;

    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 20, message = "Password must be at least 6-20 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{6,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String password;

}
