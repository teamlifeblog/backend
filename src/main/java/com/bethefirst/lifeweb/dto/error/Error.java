package com.bethefirst.lifeweb.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Error {
	private String field;
	private Object value;
	private String message;
}
