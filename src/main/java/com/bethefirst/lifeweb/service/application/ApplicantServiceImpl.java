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
import com.bethefirst.lifeweb.exception.ConflictException;
import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import com.bethefirst.lifeweb.repository.application.ApplicantAnswerRepository;
import com.bethefirst.lifeweb.repository.application.ApplicantRepository;
import com.bethefirst.lifeweb.repository.application.ApplicationRepository;
import com.bethefirst.lifeweb.repository.campaign.CampaignRepository;
import com.bethefirst.lifeweb.repository.member.MemberRepository;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {

	private final CampaignRepository campaignRepository;
	private final ApplicantRepository applicantRepository;
	private final ApplicantAnswerRepository applicantAnswerRepository;
	private final MemberRepository memberRepository;
	private final ApplicationRepository applicationRepository;

	/** 신청자 생성 */
	@Override
	public Long createApplicant(Long memberId, CreateApplicantDto createApplicationDto) {
		
		// 신청자 저장
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다. " + memberId));
		Application application = applicationRepository.findById(createApplicationDto.getApplicationId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + createApplicationDto.getApplicationId()));

		Applicant applicant = createApplicationDto.createApplicant(member, application);

		Long applicantId = applicantRepository.save(applicant).getId();

		// 신청자답변 저장
		List<ApplicationQuestion> applicationQuestionList = application.getApplicationQuestionList();
		List<ApplicantAnswerDto> applicantAnswerDtoList = createApplicationDto.getApplicantAnswerDtoList();

		if (!CollectionUtils.isEmpty(applicationQuestionList)) {
			// 신청자답변의 입력값이 부족한 경우
			int size = applicationQuestionList.size();
			if (size > createApplicationDto.getApplicationQuestionId().size()) {
				throw new UnprocessableEntityException("applicationQuestionId", createApplicationDto.getApplicationQuestionId(),
						"신청서질문ID값을 " + size + "개 입력해주세요");
			} else if (size > createApplicationDto.getAnswer().size()) {
				throw new UnprocessableEntityException("answer", createApplicationDto.getAnswer(),
						"답변을 " + size + "개 입력해주세요");
			}

			for (ApplicationQuestion applicationQuestion : applicationQuestionList) {
				for (ApplicantAnswerDto applicantAnswerDto : applicantAnswerDtoList) {
					if (applicantAnswerDto.getId().equals(applicationQuestion.getId())) {
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
		
		Applicant application = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청자입니다. " + applicantId));

		//  캠페인 상태 != 신청 || 신청서 상태 == 선정 일 떄 수정 불가
		if (!application.getApplication().getCampaign().getStatus().equals(CampaignStatus.APPLICATION)) {
			throw new ConflictException("캠페인 신청기간에만 수정할 수 있습니다");
		} else if (application.getStatus().equals(ApplicantStatus.SELECT)) {
			throw new ConflictException("선정된 신청자는 수정할 수 없습니다.");
		}

		// 신청자 수정
		application.updateApplicant(updateApplicantDto.getMemo());

		// 신청자답변 수정
		List<ApplicantAnswer> applicantAnswerList = application.getApplicantAnswerList();
		List<ApplicantAnswerDto> applicantAnswerDtoList = updateApplicantDto.getAnswerDtoList();

		if (!CollectionUtils.isEmpty(applicantAnswerList)) {
			// 신청자답변의 입력값이 부족한 경우
			int size = applicantAnswerList.size();
			if (size > updateApplicantDto.getApplicationQuestionId().size()) {
				throw new UnprocessableEntityException("applicationQuestionId", updateApplicantDto.getApplicationQuestionId(),
						"신청서질문ID값을 " + size + "개 입력해주세요");
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

		applicantRepository.updateStatus(ApplicantStatus.SELECT, updateApplicantStatusDto.getSelectApplicantId(), updateApplicantStatusDto.getCampaignId());
		applicantRepository.updateStatus(ApplicantStatus.UNSELECT, updateApplicantStatusDto.getUnselectApplicantId(), updateApplicantStatusDto.getCampaignId());

		Campaign campaign = campaignRepository.findById(updateApplicantStatusDto.getCampaignId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + updateApplicantStatusDto.getCampaignId()));

		if (campaign.getHeadcount() < campaign.getApplication().getApplicantList().size()) {
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
