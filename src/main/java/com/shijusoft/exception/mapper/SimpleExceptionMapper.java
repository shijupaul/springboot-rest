package com.shijusoft.exception.mapper;

import com.shijusoft.exception.error.ErrorDetails;
import com.shijusoft.exception.error.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Created by spaul on 21/02/2018.
 */
@RestControllerAdvice
public class SimpleExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorDetails> handleNotFoundException(RuntimeException exception, WebRequest request) {
        NotFoundException notFoundException = (NotFoundException)exception;
        ErrorDetails errorDetails = new ErrorDetails(new Date(), notFoundException.getMessage(),request.getDescription(false));
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDetails> handleAllOtherExceptions(Exception exception, WebRequest request) {
        RuntimeException runtimeException = (RuntimeException) exception;
        ErrorDetails errorDetails = new ErrorDetails(new Date(), runtimeException.getMessage(),request.getDescription(false));
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
