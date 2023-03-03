package com.bethefirst.lifeweb.dto.error;

import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ErrorsResult extends ErrorResult{

	private List<Error> errors;

	public static ErrorsResult ErrorResult(HttpStatus status, BindException e, String path) {

		String message = "유효성 검사 실패. Error count : " + e.getErrorCount();

		List<Error> errors = e.getAllErrors()
				.stream().map(error -> {
					FieldError fieldError = (FieldError) error;
					return new Error(fieldError.getField(), (String) fieldError.getRejectedValue(), fieldError.getDefaultMessage());
				})
				.collect(Collectors.toList());

		return new ErrorsResult(status, message, path, errors);
	}

	public static ErrorsResult ErrorResult(HttpStatus status, UnprocessableEntityException e, String path) {

		List<Error> errors = List.of(new Error(e.getField(), e.getValue(), e.getMessage()));

		return new ErrorsResult(status, e.getMessage(), path, errors);
	}

	private ErrorsResult(HttpStatus status, String message, String path, List<Error> errors) {
		super(status, message, path);
		this.errors = errors;
	}

}
