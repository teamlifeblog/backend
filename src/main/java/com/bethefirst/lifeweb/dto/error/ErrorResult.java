package com.bethefirst.lifeweb.dto.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResult {

    private int status;
    private String error;
    private String message;
    private String path;

	public ErrorResult(HttpStatus status, String message, String path) {
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
		this.path = path;
	}

}
