package com.bethefirst.lifeweb;

import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
public enum CustomJsonFieldType {

	LOCAL_DATE(LocalDate.class.getSimpleName()),
	LOCAL_DATE_TIME(LocalDateTime.class.getSimpleName()),
	MULTIPART_FILE(MultipartFile.class.getSimpleName());

	private String string;

	@Override
	public String toString() {
		return this.string;
	}

}
