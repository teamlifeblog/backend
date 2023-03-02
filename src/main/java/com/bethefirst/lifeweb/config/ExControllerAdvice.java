package com.bethefirst.lifeweb.config;

import com.bethefirst.lifeweb.dto.error.ErrorResult;
import com.bethefirst.lifeweb.dto.error.ErrorsResult;
import com.bethefirst.lifeweb.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExControllerAdvice {

	private final HttpServletRequest request;

	@ExceptionHandler
	@ResponseStatus(UNAUTHORIZED)//401
	public ErrorResult unauthorizedExHandler(UnauthorizedException e) {
		return new ErrorResult(UNAUTHORIZED, e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(FORBIDDEN)//403
	public ErrorResult forbiddenExHandler(ForbiddenException e) {
		return new ErrorResult(FORBIDDEN, e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(NOT_FOUND)//404
	public ErrorResult entityNotFoundExHandler(EntityNotFoundException e) {
		return new ErrorResult(NOT_FOUND, e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(CONFLICT)//409
	public ErrorResult conflictExHandler(ConflictException e) {
		return new ErrorResult(CONFLICT, e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(UNPROCESSABLE_ENTITY)//422
	public ErrorResult unprocessableEntityExHandler(UnprocessableEntityException e) {
		return ErrorsResult.ErrorResult(UNPROCESSABLE_ENTITY, e, request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(UNPROCESSABLE_ENTITY)//422
	public ErrorResult bindExHandler(BindException e) {
		return ErrorsResult.ErrorResult(UNPROCESSABLE_ENTITY, e, request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)//500
	public ErrorResult runtimeExHandler(RuntimeException e) {
		return new ErrorResult(INTERNAL_SERVER_ERROR, e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)//500
	public ErrorResult exceptionHandler(Exception e) {
		return new ErrorResult(INTERNAL_SERVER_ERROR, e.getMessage(), request.getRequestURI());
	}

}
