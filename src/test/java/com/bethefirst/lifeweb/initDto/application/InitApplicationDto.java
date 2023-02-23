package com.bethefirst.lifeweb.initDto.application;

import com.bethefirst.lifeweb.dto.application.request.CreateApplicationQuestionDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicationDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicationQuestionDto;
import com.bethefirst.lifeweb.entity.application.QuestionType;

import java.util.Arrays;
import java.util.List;

public class InitApplicationDto {

	public ApplicationDto getApplicationDto() {
		return new ApplicationDto(1L, applicationQuestionDtoList);
	}

	public CreateApplicationQuestionDto getCreateApplicationQuestionDto() {
		return new CreateApplicationQuestionDto(Arrays.asList("question 1", "question 2"), Arrays.asList(QuestionType.TEXT, QuestionType.CHECKBOX), Arrays.asList("", "111,222,333"));
	}

	private List<ApplicationQuestionDto> applicationQuestionDtoList = Arrays.asList(
			new ApplicationQuestionDto(1L, "TEXT question", QuestionType.TEXT, ""),
			new ApplicationQuestionDto(2L , "CHECKBOX question", QuestionType.CHECKBOX, "items1, items2, items3")
	);

}
