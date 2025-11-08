package com.socialapp.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) { //!403 Forbidden
            super(message);
    }
}
