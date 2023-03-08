package com.bethefirst.lifeweb.service.application;

import com.bethefirst.lifeweb.dto.application.response.ApplicationDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicationQuestionDto;
import com.bethefirst.lifeweb.entity.application.Application;
import com.bethefirst.lifeweb.entity.application.ApplicationQuestion;
import com.bethefirst.lifeweb.entity.campaign.Campaign;
import com.bethefirst.lifeweb.repository.application.ApplicationQuestionRepository;
import com.bethefirst.lifeweb.repository.application.ApplicationRepository;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final ApplicationQuestionRepository applicationQuestionRepository;

	/** 신청서 생성 */
	@Override
	public void createApplication(Campaign campaign, List<ApplicationQuestionDto> applicationQuestionDtoList) {

		// 신청서 저장
		Application application = applicationRepository.save(new Application(campaign));

		// 신청서질문 저장
		if (!applicationQuestionDtoList.isEmpty()) {
			applicationQuestionDtoList
					.forEach(applicationQuestionDto -> applicationQuestionRepository.save(applicationQuestionDto.createApplicationQuestion(application)));
		}

	}

	/** 신청서 조회 */
	@Transactional(readOnly = true)
	@Override
	public ApplicationDto getApplicationDto(Long applicationId) {
		return new ApplicationDto(applicationRepository.findById(applicationId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 신청서입니다. " + applicationId)));
	}

	/** 신청서 수정 */
	@Override
	public void updateApplication(Application application, List<ApplicationQuestionDto> applicationQuestionDtoList) {

		// 신청서질문 insert
		if (!CollectionUtils.isEmpty(applicationQuestionDtoList)) {
			applicationQuestionDtoList.stream().filter(applicationQuestionDto -> applicationQuestionDto.getId() == 0)
					.forEach(applicationQuestionDto -> applicationQuestionRepository.save(applicationQuestionDto.createApplicationQuestion(application)));
		}

		if (!CollectionUtils.isEmpty(application.getApplicationQuestionList())) {
			for (ApplicationQuestion applicationQuestion : application.getApplicationQuestionList()) {
				boolean result = false;
				if (!CollectionUtils.isEmpty(applicationQuestionDtoList)) {
					for (ApplicationQuestionDto applicationQuestionDto : applicationQuestionDtoList) {
						// 신청서질문 update
						if (applicationQuestionDto.getId().equals(applicationQuestion.getId())) {
							applicationQuestionDto.updateApplicationQuestion(applicationQuestion);
							result = true;
							break;
						}
					}
				}
				// 신청서질문 delete
				if (!result) {
					applicationQuestionRepository.delete(applicationQuestion);
				}
			}
		}

	}

}
