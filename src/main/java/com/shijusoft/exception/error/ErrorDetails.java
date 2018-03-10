package com.shijusoft.exception.error;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by spaul on 21/02/2018.
 */
public class ErrorDetails {

    private Date date;
    private String message;
    private String details;

    public ErrorDetails(Date date, String message, String details) {
        this.date = date;
        this.message = message;
        this.details = details;
    }

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:SS")
    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
