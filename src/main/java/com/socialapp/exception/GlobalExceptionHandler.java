package com.socialapp.exception;

import com.socialapp.dto.error.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(NotFoundException ex, HttpServletRequest req){
//        ErrorResponseDTO err = new ErrorResponseDTO();
//        err.setStatus(HttpStatus.NOT_FOUND.value());
//        err.setError("Not Found!");
//        err.setMessage(ex.getMessage());
//        err.setPath(req.getRequestURI());
//        return new ResponseEntity<>(err,HttpStatus.NOT_FOUND);
        //!kod tekrarı olmaması açısından en altta bir util func oluşturuldu üsttekini her blokta tekrar tekrar yazmamak için (DRY!)
        return buildErrorResponse(ex, req, HttpStatus.NOT_FOUND, "Not Found!");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(ForbiddenException ex, HttpServletRequest req){
        return buildErrorResponse(ex, req, HttpStatus.FORBIDDEN, "Forbidden!");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException ex, HttpServletRequest req){
        return buildErrorResponse(ex, req, HttpStatus.BAD_REQUEST, "Bad Request!");
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnAuthorized(UnAuthorizedException ex, HttpServletRequest req){
        return buildErrorResponse(ex, req, HttpStatus.UNAUTHORIZED, "Unauthorized!");
    }

    //!general handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
    //*util function for DRY principle :)
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(Exception ex, HttpServletRequest req,
                                                                HttpStatus status, String errorMessage) {
        ErrorResponseDTO err = new ErrorResponseDTO();
        err.setStatus(status.value());
        err.setError(errorMessage);
        err.setMessage(ex.getMessage());
        err.setPath(req.getRequestURI());
        return new ResponseEntity<>(err, status);
    }
}
