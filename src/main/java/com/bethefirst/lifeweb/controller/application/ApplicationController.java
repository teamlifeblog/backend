package com.bethefirst.lifeweb.controller.application;

import com.bethefirst.lifeweb.dto.application.request.CreateApplicationQuestionDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicationDto;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

	private final ApplicationService applicationService;

	/** 신청서 조회 */
	@GetMapping("/{applicationId}")
	public ApplicationDto read(@PathVariable Long applicationId) {
		return applicationService.getApplicationDto(applicationId);
	}

	/** 신청서 질문 추가 */
	@PostMapping("/{applicationId}/question")
	public ResponseEntity<?> createApplicationQuestion(@PathVariable Long applicationId,
													   @Valid @RequestBody CreateApplicationQuestionDto createApplicationQuestionDto) {

		// 신청서질문 저장
		applicationService.createApplicationQuestion(applicationId, createApplicationQuestionDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/applications/" + applicationId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

}
