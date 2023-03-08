package com.bethefirst.lifeweb.service.application.interfaces;

import com.bethefirst.lifeweb.dto.application.request.CreateApplicationQuestionDto;

public interface ApplicationQuestionService {

	/** 신청서 질문 추가 */
	void addApplicationQuestion(Long applicationId, CreateApplicationQuestionDto createApplicationQuestionDto);

}
