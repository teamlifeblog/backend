package com.bethefirst.lifeweb.dto.application.response;

import com.bethefirst.lifeweb.entity.application.Application;
import com.bethefirst.lifeweb.entity.application.ApplicationQuestion;
import com.bethefirst.lifeweb.entity.application.QuestionType;
import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ApplicationQuestionDto {

	private Long id;//질문ID
	private String question;//질문
	private QuestionType type;//유형
	private String items;//항목

	public ApplicationQuestionDto(ApplicationQuestion applicationQuestion) {
		id = applicationQuestion.getId();
		question = applicationQuestion.getQuestion();
		type = applicationQuestion.getType();
		items = applicationQuestion.getItems();
	}

	public ApplicationQuestionDto(Long applicationQuestionId, String question, QuestionType type, String items) {
		questionVerification(type, items);
		this.id = applicationQuestionId;
		this.question = question;
		this.type = type;
		this.items = items;
	}

	public ApplicationQuestionDto(String question, QuestionType type, String items) {
		questionVerification(type, items);
		this.question = question;
		this.type = type;
		this.items = items;
	}

	public List<ApplicationQuestionDto> getApplicationQuestionDtoList(List<String> question, List<QuestionType> type, List<String> items) {
		List<ApplicationQuestionDto> list = new ArrayList<>();
		for (int i = 0; i < question.size(); i++) {
			list.add(new ApplicationQuestionDto(question.get(i), type.get(i), items.get(i)));
		}
		return list;
	}

	// 질문 검증
	private void questionVerification(QuestionType type, String items) {
		if ((type.equals(QuestionType.TEXT) || type.equals(QuestionType.TEXTAREA)) && !items.isBlank()) {
			log.error("items : {} ", items);
		} else if ((type.equals(QuestionType.CHECKBOX) || type.equals(QuestionType.RADIO)) && items.isBlank()) {
			throw new UnprocessableEntityException("items", items, "질문 유형이 단일선택 또는 복수선택일 때는 항목을 값이 필요합니다.");
		}
	}

	/** 질문 생성 */
	public ApplicationQuestion createApplicationQuestion(Application application) {
		return new ApplicationQuestion(application, question, type,
					type == QuestionType.RADIO || type == QuestionType.CHECKBOX ? items : null);
	}

	/** 질문 수정 */
	public void updateApplicationQuestion(ApplicationQuestion applicationQuestion) {
		applicationQuestion.updateApplicationQuestion(question, type,
				items == null ? null : items);
	}

}
