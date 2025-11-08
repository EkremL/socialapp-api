package com.socialapp.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){ //!400 Bad Request
        super(message); //!Parent constructor’ını çağırıp hata mesajını üst sınıfa iletmek için.
    }
}
