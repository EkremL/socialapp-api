package com.socialapp.dto.error;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {

    private LocalDateTime timeStamp = LocalDateTime.now();

    private int status;

    private String error;

    private String message;

    private String path;
}
