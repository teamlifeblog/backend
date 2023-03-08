package com.bethefirst.lifeweb.dto.application.response;

import com.bethefirst.lifeweb.entity.application.ApplicantAnswer;
import com.bethefirst.lifeweb.entity.application.Applicant;
import com.bethefirst.lifeweb.entity.application.ApplicationQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicantAnswerDto {

	private Long id;//신청자답변ID
	private Long applicationQuestionId;//질문ID
	private String answer;//답변

	public ApplicantAnswerDto(ApplicantAnswer applicantAnswer) {
		this.id = applicantAnswer.getId();
		this.applicationQuestionId = applicantAnswer.getApplicationQuestion().getId();
		this.answer = applicantAnswer.getAnswer();
	}

	public ApplicantAnswerDto(String answer, Long applicationQuestionId) {
		this.applicationQuestionId = applicationQuestionId;
		this.answer = answer;
	}

	public ApplicantAnswerDto(Long answerId, String answer) {
		this.id = answerId;
		this.answer = answer;
	}

	/** 신청자답변 생성 */
	public ApplicantAnswer createAnswer(Applicant applicant, ApplicationQuestion applicationQuestion) {
		return new ApplicantAnswer(applicant, applicationQuestion, answer);
	}

}
