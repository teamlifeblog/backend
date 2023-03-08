package com.bethefirst.lifeweb.service.application;

import com.bethefirst.lifeweb.dto.application.request.CreateApplicationQuestionDto;
import com.bethefirst.lifeweb.entity.application.Application;
import com.bethefirst.lifeweb.entity.campaign.CampaignStatus;
import com.bethefirst.lifeweb.exception.ConflictException;
import com.bethefirst.lifeweb.repository.application.ApplicationQuestionRepository;
import com.bethefirst.lifeweb.repository.application.ApplicationRepository;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicationQuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApplicationQuestionServiceImpl implements ApplicationQuestionService {

	private final ApplicationQuestionRepository applicationQuestionRepository;
	private final ApplicationRepository applicationRepository;

	/** 신청서 질문 추가 */
	@Override
	public void addApplicationQuestion(Long applicationId, CreateApplicationQuestionDto createApplicationQuestionDto) {

		Application application = applicationRepository.findWithCampaignById(applicationId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청서입니다. " + applicationId));

		// 캠페인상태가 대기일 때만 질문 추가 가능
		if (!application.getCampaign().getStatus().equals(CampaignStatus.STAND)) {
			throw new ConflictException("캠페인 대기기간에만 수정할 수 있습니다");
		}

		// 신청서질문 저장
		createApplicationQuestionDto.getApplicationQuestionDtoList()
				.forEach(applicationQuestionDto -> applicationQuestionRepository.save(applicationQuestionDto.createApplicationQuestion(application)));

	}

}
