package com.bethefirst.lifeweb.exception;

import lombok.Getter;

@Getter
public class UnprocessableEntityException extends IllegalStateException {

	private String field;
	private Object value;

	public UnprocessableEntityException(String field, Object value, String message) {
		super(message);
		this.field = field;
		if (value == null) {
			this.value = null;
		} else if (value instanceof Boolean || value instanceof Number) {
			this.value = value;
		} else {
			this.value = value;
		}
	}

}
