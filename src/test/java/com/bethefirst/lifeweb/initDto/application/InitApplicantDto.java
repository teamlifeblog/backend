package com.bethefirst.lifeweb.initDto.application;

import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantStatusDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicantAnswerDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.ApplicantSearchRequirements;
import com.bethefirst.lifeweb.dto.application.request.CreateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantDto;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitApplicantDto {

	public CreateApplicantDto getCreateApplicantDto() {
		return new CreateApplicantDto(1L, "메모", Arrays.asList(1L, 2L), Arrays.asList("대답1", "대답2"));
	}

	public ApplicantDto getApplicantDto() {
		return getApplicantDtoList().get(0);
	}

	public ApplicantSearchRequirements getSearchRequirements() {
		return ApplicantSearchRequirements.builder()
				.pageable(pageable)
				.build();
	}

	public Page<ApplicantDto> getApplicantDtoPage() {
		return new PageImpl(getSubList(), pageable, getApplicantDtoList().size());
	}

	private List<ApplicantDto> getSubList() {
		if (getApplicantDtoList().size() < getPage() * getSize()) {
			return new ArrayList<>();
		} else if (getApplicantDtoList().size() - getPage() * getSize() < getSize()) {
			return getApplicantDtoList().subList(getPage() * getSize(), getApplicantDtoList().size());
		} else {
			return getApplicantDtoList().subList(getPage() * getSize(), getSize());
		}
	}

	public List<ApplicantDto> getApplicantDtoList() {

		List<ApplicantDto> list = new ArrayList<>();

		for (int i = 0; i < 15; i++) {
			list.add(new ApplicantDto((long) i, "메모", LocalDateTime.now(), ApplicantStatus.UNSELECT, applicantAnswerDtoList));
		}

		return list;
	}

	public UpdateApplicantDto getUpdateApplicantDto() {
		return new UpdateApplicantDto("수정된 메모", Arrays.asList(1L, 2L), Arrays.asList("수정된 대답1", "수정된 대답2"));
	}

	public UpdateApplicantStatusDto getUpdateApplicantStatusDto() {
		return new UpdateApplicantStatusDto(1L, Arrays.asList(1L, 2L), Arrays.asList(3L, 4L));
	}

	private List<ApplicantAnswerDto> applicantAnswerDtoList = Arrays.asList(
			new ApplicantAnswerDto(1L, "대답1"),
			new ApplicantAnswerDto(2L, "대답2")
	);

	private Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "created"));

	private int getPage() {
		return pageable.getPageNumber();
	}

	private int getSize() {
		return pageable.getPageSize();
	}

}
