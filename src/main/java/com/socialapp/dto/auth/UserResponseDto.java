package com.socialapp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//! /users/{id} endpointiyle profile sayfasında görüntülenecek bilgiler için bu dto classını yazdım.
public class UserResponseDto {

    private String username;
    private String role;
    private LocalDateTime createdAt;
}
