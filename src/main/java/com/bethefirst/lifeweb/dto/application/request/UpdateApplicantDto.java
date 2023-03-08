package com.bethefirst.lifeweb.dto.application.request;

import com.bethefirst.lifeweb.dto.application.response.ApplicantAnswerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicantDto {

	private String memo;//메모
	private List<Long> applicantAnswerId;//신청자답변ID
	private List<String> answer;//답변

	public List<ApplicantAnswerDto> getAnswerDtoList() {
		List<ApplicantAnswerDto> list = new ArrayList<>();
		for (int i = 0; i < answer.size(); i++) {
			list.add(new ApplicantAnswerDto(applicantAnswerId.get(i), answer.get(i)));
		}
		return list;
	}

}
