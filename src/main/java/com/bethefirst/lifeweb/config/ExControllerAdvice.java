package com.bethefirst.lifeweb.config;

import com.bethefirst.lifeweb.dto.ErrorResult;
import com.bethefirst.lifeweb.exception.ConflictException;
import com.bethefirst.lifeweb.exception.ForbiddenException;
import com.bethefirst.lifeweb.exception.UnauthorizedException;
import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResult illegalExHandler(IllegalArgumentException e){
		return new ErrorResult("IllegalArgumentException" , e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResult bindExHandler(BindException e) {
		StringBuilder message = new StringBuilder();
		e.getBindingResult().getFieldErrors().forEach(fieldError -> message.append(fieldError.getDefaultMessage()));
		return new ErrorResult("BindException" , message.toString());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResult unauthorizedExHandler(UnauthorizedException e) {
		return new ErrorResult("UnauthorizedException" , e.getMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler
	public ErrorResult forbiddenExHandler(ForbiddenException e) {
		return new ErrorResult("ForbiddenException" , e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResult entityNotFoundExHandler(EntityNotFoundException e) {
		return new ErrorResult("EntityNotFoundException" , e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResult conflictExHandler(ConflictException e) {
		return new ErrorResult("ConflictException" , e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorResult unprocessableEntityExHandler(UnprocessableEntityException e) {
		return new ErrorResult("UnprocessableEntityException" , e.getMessage());
	}


	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResult runtimeExHandler(RuntimeException e) {
		return new ErrorResult("RuntimeException" , e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResult exceptionHandler(Exception e) {
		return new ErrorResult("Exception" , e.getMessage());
	}

}


