package com.myorg.holidayservice.exception;

public class HolidayException extends RuntimeException {
    public HolidayException(String message, Throwable cause) {
        super(message, cause);
    }
}

