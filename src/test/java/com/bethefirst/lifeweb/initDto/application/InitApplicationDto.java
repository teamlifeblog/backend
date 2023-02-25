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
		return new CreateApplicationQuestionDto(Arrays.asList("질문1", "질문2"), Arrays.asList(QuestionType.TEXT, QuestionType.CHECKBOX), Arrays.asList("", "아이템1,아이템2,아이템3"));
	}

	private List<ApplicationQuestionDto> applicationQuestionDtoList = Arrays.asList(
			new ApplicationQuestionDto(1L, "단답형", QuestionType.TEXT, ""),
			new ApplicationQuestionDto(2L , "복수선택", QuestionType.CHECKBOX, "아이템1, 아이템2, 아이템3")
	);

}
