package com.myorg.holidayservice.exception;

public class NoHolidaysFoundException extends RuntimeException {

    public NoHolidaysFoundException(String message) {
        super(message);
    }
}

