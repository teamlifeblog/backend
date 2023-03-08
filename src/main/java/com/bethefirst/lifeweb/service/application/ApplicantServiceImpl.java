package com.bethefirst.lifeweb.service.application;

import com.bethefirst.lifeweb.dto.application.request.ApplicantSearchRequirements;
import com.bethefirst.lifeweb.dto.application.request.CreateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantStatusDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicantAnswerDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicantDto;
import com.bethefirst.lifeweb.entity.application.*;
import com.bethefirst.lifeweb.entity.application.ApplicationQuestion;
import com.bethefirst.lifeweb.entity.campaign.Campaign;
import com.bethefirst.lifeweb.entity.campaign.CampaignStatus;
import com.bethefirst.lifeweb.entity.member.Member;
import com.bethefirst.lifeweb.entity.review.Review;
import com.bethefirst.lifeweb.exception.ConflictException;
import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import com.bethefirst.lifeweb.repository.application.ApplicantAnswerRepository;
import com.bethefirst.lifeweb.repository.application.ApplicantRepository;
import com.bethefirst.lifeweb.repository.application.ApplicationRepository;
import com.bethefirst.lifeweb.repository.campaign.CampaignRepository;
import com.bethefirst.lifeweb.repository.member.MemberRepository;
import com.bethefirst.lifeweb.repository.review.ReviewRepository;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {

	private final CampaignRepository campaignRepository;
	private final ApplicationRepository applicationRepository;
	private final ApplicantRepository applicantRepository;
	private final ApplicantAnswerRepository applicantAnswerRepository;
	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;

	/** 신청자 생성 */
	@Override
	public Long createApplicant(Long memberId, CreateApplicantDto createApplicationDto) {

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다. " + memberId));
		Application application = applicationRepository.findWithQuestionAndCampaignById(createApplicationDto.getApplicationId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청서입니다. " + createApplicationDto.getApplicationId()));

		if (!application.getCampaign().getStatus().equals(CampaignStatus.APPLICATION)) {
			throw new ConflictException("캠페인 신청기간이 아닙니다.");
		}

		application.getApplicantList()
				.forEach(applicant -> {
					if (applicant.getMember().getId().equals(memberId)) {
						throw new ConflictException("이미 신청하였습니다.");
					}
				});

		// 신청자 저장
		Applicant applicant = createApplicationDto.createApplicant(member, application);

		Long applicantId = applicantRepository.save(applicant).getId();

		// 신청자답변 저장
		List<ApplicationQuestion> applicationQuestionList = application.getApplicationQuestionList();
		List<ApplicantAnswerDto> applicantAnswerDtoList = createApplicationDto.getApplicantAnswerDtoList();

		if (!CollectionUtils.isEmpty(applicationQuestionList)) {
			// 신청자답변의 입력값이 부족한 경우
			int size = applicationQuestionList.size();
			List<Long> questionIdList = applicationQuestionList.stream().map(ApplicationQuestion::getId).toList();
			if (size > createApplicationDto.getApplicationQuestionId().size()) {
				throw new UnprocessableEntityException("applicationQuestionId", createApplicationDto.getApplicationQuestionId(),
						"신청서질문ID값을 " + size + "개 입력해주세요");
			} else if (!createApplicationDto.getApplicationQuestionId().equals(questionIdList)) {
				throw new UnprocessableEntityException("applicationQuestionId", createApplicationDto.getApplicationQuestionId(),
						"신청서질문ID를 잘못 입력하였습니다.");
			} else if (size > createApplicationDto.getAnswer().size()) {
				throw new UnprocessableEntityException("answer", createApplicationDto.getAnswer(),
						"답변을 " + size + "개 입력해주세요");
			}

			for (ApplicationQuestion applicationQuestion : applicationQuestionList) {
				for (ApplicantAnswerDto applicantAnswerDto : applicantAnswerDtoList) {
					if (applicantAnswerDto.getApplicationQuestionId().equals(applicationQuestion.getId())) {
						applicantAnswerRepository.save(applicantAnswerDto.createAnswer(applicant, applicationQuestion));
						break;
					}
				}
			}
		}

		return applicantId;
	}

	/** 신청자 조회 */
	@Transactional(readOnly = true)
	@Override
	public ApplicantDto getApplicantDto(Long applicantId) {
		return new ApplicantDto(applicantRepository.findById(applicantId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청자입니다. " + applicantId)));
	}

	/** 신청자 리스트 조회 */
	@Transactional(readOnly = true)
	@Override
	public Page<ApplicantDto> getApplicantDtoList(ApplicantSearchRequirements searchRequirements) {
		return applicantRepository.findAllBySearchRequirements(searchRequirements).map(ApplicantDto::new);
	}

	/** 신청자 수정 */
	@Override
	public void updateApplicant(Long applicantId, UpdateApplicantDto updateApplicantDto) {
		
		Applicant applicant = applicantRepository.findWithAnswerAndCampaignById(applicantId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청자입니다. " + applicantId));

		//  캠페인 상태 != 신청 || 신청서 상태 == 선정 일 떄 수정 불가
		if (!applicant.getApplication().getCampaign().getStatus().equals(CampaignStatus.APPLICATION)) {
			throw new ConflictException("캠페인 신청기간에만 수정할 수 있습니다");
		} else if (applicant.getStatus().equals(ApplicantStatus.SELECT)) {
			throw new ConflictException("선정된 신청자는 수정할 수 없습니다.");
		}

		// 신청자 수정
		applicant.updateApplicant(updateApplicantDto.getMemo());

		// 신청자답변 수정
		List<ApplicantAnswer> applicantAnswerList = applicant.getApplicantAnswerList();
		List<ApplicantAnswerDto> applicantAnswerDtoList = updateApplicantDto.getAnswerDtoList();

		if (!CollectionUtils.isEmpty(applicantAnswerList)) {
			// 신청자답변의 입력값이 부족한 경우
			int size = applicantAnswerList.size();
			List<Long> answerIdList = applicantAnswerList.stream().map(ApplicantAnswer::getId).toList();
			if (size > updateApplicantDto.getApplicantAnswerId().size()) {
				throw new UnprocessableEntityException("applicantAnswerId", updateApplicantDto.getApplicantAnswerId(),
						"신청자답변ID값을 " + size + "개 입력해주세요");
			} else if (!updateApplicantDto.getApplicantAnswerId().equals(answerIdList)) {
				throw new UnprocessableEntityException("applicantAnswerId", updateApplicantDto.getApplicantAnswerId(),
						"신청서답변ID를 잘못 입력하였습니다.");
			} else if (size > updateApplicantDto.getAnswer().size()) {
				throw new UnprocessableEntityException("answer", updateApplicantDto.getAnswer(),
						"답변을 " + size + "개 입력해주세요");
			}

			for (ApplicantAnswer applicantAnswer : applicantAnswerList) {
				for (ApplicantAnswerDto applicantAnswerDto : applicantAnswerDtoList) {
					if (applicantAnswerDto.getId().equals(applicantAnswer.getId())) {
						applicantAnswer.updateApplicationAnswer(applicantAnswerDto.getAnswer());
						break;
					}
				}
			}
		}

	}

	/** 신청자 상태 수정 */
	@Override
	public void updateStatus(UpdateApplicantStatusDto updateApplicantStatusDto) {

		Long campaignId = updateApplicantStatusDto.getCampaignId();
		Campaign campaign = campaignRepository.findWithApplicantById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + campaignId));

		// 캠페인에 맞는 신청자ID 인지 확인
		List<Applicant> applicantList = campaign.getApplication().getApplicantList();
		List<Long> applicantIdList = applicantList
				.stream().map(Applicant::getId)
				.toList();

		List<Long> selectApplicantId = new ArrayList<>(updateApplicantStatusDto.getSelectApplicantId());
		selectApplicantId.removeAll(applicantIdList);
		if (selectApplicantId.size() > 0) {
			throw new UnprocessableEntityException("selectApplicantId", selectApplicantId, "캠페인ID와 맞지 않는 신청자ID입니다.");
		}
		List<Long> unselectApplicantId = new ArrayList<>(updateApplicantStatusDto.getUnselectApplicantId());
		unselectApplicantId.removeAll(applicantIdList);
		if (unselectApplicantId.size() > 0) {
			throw new UnprocessableEntityException("unselectApplicantId", unselectApplicantId, "캠페인ID와 맞지 않는 신청자ID입니다.");
		}

		// 신청자가 리뷰를 안 썼는지 확인
		List<Long> errorList = reviewRepository.findAllByMemberApplicantListIdInAndCampaignId(updateApplicantStatusDto.getUnselectApplicantId(), campaignId)
//				.stream().map(review -> review.getMember().getApplicantList()
//						.stream().filter(applicant -> applicant.getApplication().getCampaign().getId().equals(campaignId))
//						.map(Applicant::getId).findFirst()
//						.orElse(null))
				.stream().map(Review::getId)
				.toList();

		if (errorList.size() > 0) {
			throw new UnprocessableEntityException("unselectApplicantId", unselectApplicantId, "리뷰를 작성한 신청자는 미선정 상태로 변경할수 없습니다.");
		}

		// 신청자 상태 수정
		if (!updateApplicantStatusDto.getSelectApplicantId().isEmpty()) {
			applicantRepository.updateStatus(ApplicantStatus.SELECT, updateApplicantStatusDto.getSelectApplicantId());
		}
		if (!updateApplicantStatusDto.getUnselectApplicantId().isEmpty()) {
			applicantRepository.updateStatus(ApplicantStatus.UNSELECT, updateApplicantStatusDto.getUnselectApplicantId());
		}

		if (campaign.getHeadcount() < applicantRepository.countByApplicationCampaignIdAndStatus(campaignId, ApplicantStatus.SELECT)) {
			throw new ConflictException("신청자 선정은 " + campaign.getHeadcount() + "명까지만 가능합니다.");
		}

	}

	/** 신청자 삭제 */
	@Override
	public void deleteApplicant(Long applicantId) {

		// 신청자 조회
		applicantRepository.findById(applicantId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청자입니다. " + applicantId));

		// 신청자 삭제
		applicantRepository.deleteById(applicantId);
	}

}
