package com.bethefirst.lifeweb.repository.application.querydsl;

import com.bethefirst.lifeweb.entity.application.Application;
import com.bethefirst.lifeweb.repository.application.ApplicationRepositoryQueryDsl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.bethefirst.lifeweb.entity.application.QApplication.application;
import static com.bethefirst.lifeweb.entity.application.QApplicant.applicant;
import static com.bethefirst.lifeweb.entity.campaign.QCampaign.campaign;
import static com.bethefirst.lifeweb.entity.campaign.QCampaignLocal.campaignLocal;

@RequiredArgsConstructor
public class ApplicationRepositoryQueryDslImpl implements ApplicationRepositoryQueryDsl {

	private final JPAQueryFactory queryFactory;

	/** 신청서, 캠페인 조회 */
	@Override
	public Optional<Application> findWithCampaignById(Long applicationId) {
		return queryFactory
				.select(application)
				.from(application)
				.join(application.campaign, campaign).fetchJoin()
				.leftJoin(campaign.campaignLocal, campaignLocal).fetchJoin()
				.where(
						applicationIdEq(applicationId)
				)
				.fetch().stream().findFirst();
	}

	/** 신청서, 신청서질문, 캠페인 조회 */
	@Override
	public Optional<Application> findWithQuestionAndCampaignById(Long applicationId) {
		return queryFactory
				.select(application)
				.from(application)
				.leftJoin(application.applicantList, applicant).fetchJoin()
				.join(application.campaign, campaign).fetchJoin()
				.leftJoin(campaign.campaignLocal, campaignLocal).fetchJoin()
				.where(
						applicationIdEq(applicationId)
				)
				.fetch().stream().findFirst();
	}

	/** 신청자 */
	private BooleanExpression applicationIdEq(Long applicationId) {
		return applicationId == null ? null : application.id.eq(applicationId);
	}
	
}
