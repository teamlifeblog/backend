package com.bethefirst.lifeweb.repository.application;

import com.bethefirst.lifeweb.dto.application.request.ApplicantSearchRequirements;
import com.bethefirst.lifeweb.entity.application.Applicant;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ApplicantRepositoryQueryDsl {

	/** 신청자, 신청자답변, 캠페인 조회 */
	Optional<Applicant> findWithAnswerAndCampaignById(Long applicantId);

	/** 신청자 리스트 조회 */
	Page<Applicant>  findAllBySearchRequirements(ApplicantSearchRequirements searchRequirements);

}
