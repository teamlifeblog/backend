package com.bethefirst.lifeweb.repository.application;

import com.bethefirst.lifeweb.entity.application.Applicant;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long>, ApplicantRepositoryQueryDsl {

	/** 신청자 조회 */
	@Override
	@EntityGraph(attributePaths = { "member", "applicantAnswerList" })
	Optional<Applicant> findById(Long applicantId);

	/** 회원ID와 캠페인ID가 같은 신청서를 조회 */
	Optional<Applicant> findByMemberIdAndApplicationCampaignId(Long memberId, Long campaignId);

	/** 신청자 상태 수정 */
	@Modifying(clearAutomatically = true)
	@Query("update Applicant a " +
			"set a.status = :status " +
			"where a.id in :applicantIdList ")
	void updateStatus(@Param("status") ApplicantStatus status,
					  @Param("applicantIdList") List<Long> applicantIdList);

	/** 선정된 신청자 수 */
	long countByApplicationCampaignIdAndStatus(Long campaignId, ApplicantStatus status);
	
}
