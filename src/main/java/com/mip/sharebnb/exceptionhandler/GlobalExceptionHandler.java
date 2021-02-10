package com.mip.sharebnb.exceptionhandler;

import com.mip.sharebnb.dto.ErrorDto;
import com.mip.sharebnb.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleDataNotFoundException(DataNotFoundException ex){

        return ErrorDto.of(ex.getMessage());
    }

    @ExceptionHandler(DuplicateValueExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDuplicateValueException(DuplicateValueExeption ex){

        return ErrorDto.of(ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleInvalidInputException(InvalidInputException ex){

        return ErrorDto.of(ex.getMessage());
    }
}
