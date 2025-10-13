package com.socialapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDto {
    //!Password değiştirirken yeni girilen şifrenin de validasyonlara uygun olmasını sağlayarak aynı regexi burda da tanımladım.
    @NotBlank(message = "Current password is required!")
    private String currentPassword;

    @NotBlank(message = "New password is required!")
    @Size(min = 6, max = 20, message = "Password must be at least 6-20 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{6,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String newPassword;
}
