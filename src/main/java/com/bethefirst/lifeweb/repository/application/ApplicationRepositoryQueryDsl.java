package com.bethefirst.lifeweb.repository.application;

import com.bethefirst.lifeweb.entity.application.Application;

import java.util.Optional;

public interface ApplicationRepositoryQueryDsl {

	/** 신청서, 캠페인 조회 */
	Optional<Application> findWithCampaignById(Long applicationId);

	/** 신청서, 신청서질문, 캠페인 조회 */
	Optional<Application> findWithQuestionAndCampaignById(Long applicationId);

}
