package com.mip.sharebnb.exceptionhandler;

import com.mip.sharebnb.dto.ErrorDto;
import com.mip.sharebnb.exception.DuplicateDateException;
import com.mip.sharebnb.exception.NotFoundAccommodation;
import com.mip.sharebnb.exception.NotFoundMemberException;
import com.mip.sharebnb.exception.UnValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationHandler {

    @ExceptionHandler(NotFoundMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleNotFoundMemberExceptionException(NotFoundMemberException ex){

        return ErrorDto.of(ex.getMessage());

    }

    @ExceptionHandler(NotFoundAccommodation.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorDto handleNotFoundAccommodationException(NotFoundAccommodation ex){

        return ErrorDto.of(ex.getMessage());

    }

    @ExceptionHandler(DuplicateDateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleDuplicateDateException(DuplicateDateException ex){

        return ErrorDto.of(ex.getMessage());

    }

    @ExceptionHandler(UnValidException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    public ErrorDto handleUnValidExceptionException(UnValidException ex){

        return ErrorDto.of(ex.getMessage());

    }
}
