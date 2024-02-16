package com.setronica.eventing.exceptions;

public class EventScheduleAlreadyExists extends BaseApplicationException {
    public EventScheduleAlreadyExists() {
    }

    public EventScheduleAlreadyExists(String message) {
        super(message);
    }

    public EventScheduleAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public EventScheduleAlreadyExists(Throwable cause) {
        super(cause);
    }

    public EventScheduleAlreadyExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}