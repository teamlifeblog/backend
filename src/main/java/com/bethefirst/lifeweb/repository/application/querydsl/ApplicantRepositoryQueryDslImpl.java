package com.bethefirst.lifeweb.repository.application.querydsl;

import com.bethefirst.lifeweb.dto.application.request.ApplicantSearchRequirements;
import com.bethefirst.lifeweb.entity.application.Applicant;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import com.bethefirst.lifeweb.repository.application.ApplicantRepositoryQueryDsl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static com.bethefirst.lifeweb.entity.application.QApplicant.applicant;
import static com.bethefirst.lifeweb.entity.application.QApplicantAnswer.applicantAnswer;
import static com.bethefirst.lifeweb.entity.application.QApplication.application;
import static com.bethefirst.lifeweb.entity.campaign.QCampaign.campaign;
import static com.bethefirst.lifeweb.entity.campaign.QCampaignLocal.campaignLocal;

@RequiredArgsConstructor
public class ApplicantRepositoryQueryDslImpl implements ApplicantRepositoryQueryDsl {

	private final JPAQueryFactory queryFactory;

	/** 신청자, 신청자답변, 캠페인 조회 */
	@Override
	public Optional<Applicant> findWithAnswerAndCampaignById(Long applicantId) {
		return queryFactory
				.select(applicant)
				.from(applicant)
				.join(applicant.applicantAnswerList, applicantAnswer).fetchJoin()
				.join(applicant.application, application).fetchJoin()
				.join(application.campaign, campaign).fetchJoin()
				.leftJoin(campaign.campaignLocal, campaignLocal).fetchJoin()
				.where(
						applicantIdEq(applicantId)
				)
				.fetch().stream().findFirst();
	}

	/** 신청자 리스트 조회 */
	@Override
	public Page<Applicant> findAllBySearchRequirements(ApplicantSearchRequirements searchRequirements) {

		// content
		List<Applicant> content = queryFactory
				.select(applicant)
				.from(applicant)
				.where(
						memberIdEq(searchRequirements.getMemberId()),
						campaignIdEq(searchRequirements.getCampaignId()),
						statusEq(searchRequirements.getStatus())
				)
				.offset(searchRequirements.getPageable().getOffset())
				.limit(searchRequirements.getPageable().getPageSize())
				.fetch();

		// size
		Long count = queryFactory
				.select(applicant.count())
				.from(applicant)
				.where(
						memberIdEq(searchRequirements.getMemberId()),
						campaignIdEq(searchRequirements.getCampaignId()),
						statusEq(searchRequirements.getStatus())
				)
				.fetchOne();

		return new PageImpl<>(content, searchRequirements.getPageable(), count);
	}


	/** 신청자 */
	private Predicate applicantIdEq(Long applicantId) {
		return applicantId == null ? null : applicant.id.eq(applicantId);
	}

	/** 맴버 */
	private BooleanExpression memberIdEq(Long memberId) {
		return memberId == null ? null : applicant.member.id.eq(memberId);
	}

	/** 캠페인 */
	private BooleanExpression campaignIdEq(Long campaignId) {
		return campaignId == null ? null : applicant.application.campaign.id.eq(campaignId);
	}

	/** 상태 */
	private BooleanExpression statusEq(ApplicantStatus status) {
		return status == null ? null : applicant.status.eq(status);
	}
	
}
