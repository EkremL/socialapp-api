package com.socialapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    //!Gerekli validasyonları sağlayarak veri güvenliğini sağladım.
    @NotBlank(message = "Username is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 20, message = "Password must be at least 6-20 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{6,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String password;
}
