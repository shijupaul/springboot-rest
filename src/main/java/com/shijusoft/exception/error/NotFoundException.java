package com.shijusoft.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by spaul on 21/02/2018.
 *
 * If we don't define ExceptionMapper then client will receive BAD_REQUEST
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundException  extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
