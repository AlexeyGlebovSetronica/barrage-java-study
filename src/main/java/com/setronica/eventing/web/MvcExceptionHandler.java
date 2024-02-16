package com.setronica.eventing.web;

import com.setronica.eventing.dto.ApplicationExceptionDto;
import com.setronica.eventing.exceptions.ApplicationLogicException;
import com.setronica.eventing.exceptions.EventScheduleAlreadyExists;
import com.setronica.eventing.exceptions.NotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler({ ApplicationLogicException.class })
    public ResponseEntity<ApplicationExceptionDto> handleApplicationLogicException(ApplicationLogicException ex) {
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ApplicationExceptionDto> handleNotFoundException(NotFoundException ex) {
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler({EventScheduleAlreadyExists.class})
    public ResponseEntity<ApplicationExceptionDto> handleEventScheduleAlreadyExists(EventScheduleAlreadyExists ex) {
        ApplicationExceptionDto exceptionDto = new ApplicationExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatusCode.valueOf(400));
    }
}
