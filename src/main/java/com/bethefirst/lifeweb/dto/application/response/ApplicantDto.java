package com.bethefirst.lifeweb.dto.application.response;

import com.bethefirst.lifeweb.entity.application.Applicant;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApplicantDto {

	private Long id;//신청자ID
	private Long memberId;//회원ID
	private String memo;//메모
	private LocalDateTime created;//신청일
	private ApplicantStatus status;//상태

	private List<ApplicantAnswerDto> applicantAnswerDtoList;//신청자답변

	public ApplicantDto(Applicant applicant) {

		id = applicant.getId();
		memberId = applicant.getMember().getId();
		memo = applicant.getMemo();
		created = applicant.getCreated();
		status = applicant.getStatus();

		applicantAnswerDtoList = applicant.getApplicantAnswerList()
				.stream().map(ApplicantAnswerDto::new)
				.collect(Collectors.toList());

	}

}
