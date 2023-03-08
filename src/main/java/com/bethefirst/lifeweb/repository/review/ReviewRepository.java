package com.bethefirst.lifeweb.repository.review;

import com.bethefirst.lifeweb.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> , ReviewRepositoryQueryDsl {

	/** 리뷰 리스트 조회 */
	List<Review> findAllByMemberApplicantListIdInAndCampaignId(List<Long> applicantId, Long campaignId);

}
