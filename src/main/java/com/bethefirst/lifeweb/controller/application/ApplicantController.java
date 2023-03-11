package com.bethefirst.lifeweb.controller.application;

import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantStatusDto;
import com.bethefirst.lifeweb.dto.application.response.ApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.ApplicantSearchRequirements;
import com.bethefirst.lifeweb.dto.application.request.CreateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantDto;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicantService;
import com.bethefirst.lifeweb.util.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("applicants")
@RequiredArgsConstructor
@Slf4j
public class ApplicantController {

	private final ApplicantService applicantService;

	/** 신청자 생성 */
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody CreateApplicantDto createApplicantDto) {

		Long memberId = SecurityUtil.getCurrentMemberId();

		// 신청자 생성
		Long applicantId = applicantService.createApplicant(memberId, createApplicantDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/applicants/" + applicantId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	/** 신청자 조회 */
	@GetMapping("/{applicantId}")
	@PostAuthorize("@webSecurity.checkAuthority(returnObject.memberId)")
	public ApplicantDto read(@PathVariable Long applicantId) {
		return applicantService.getApplicantDto(applicantId);
	}

	/** 신청자 리스트 조회 */
	@GetMapping
	@PreAuthorize("@webSecurity.checkAuthority(#searchRequirements.memberId)")
	public Page<ApplicantDto> readAll(ApplicantSearchRequirements searchRequirements,
									  @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		searchRequirements.setPageable(pageable);
		return applicantService.getApplicantDtoList(searchRequirements);
	}

	/** 신청자 수정 */
	@PutMapping("/{applicantId}")
	@PostAuthorize("@webSecurity.checkAuthority(returnObject.memberId)")
	public ResponseEntity<?> update(@PathVariable Long applicantId,
									@Valid @RequestBody UpdateApplicantDto updateApplicantDto) {

		// 신청자 수정
		applicantService.updateApplicant(applicantId, updateApplicantDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/applicants/" + applicantId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	/** 신청자 상태 수정 */
	@PutMapping("/status")
	public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateApplicantStatusDto updateApplicantStatusDto) {

		// 신청자 상태 수정
		applicantService.updateStatus(updateApplicantStatusDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/applicants");

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	/** 신청자 삭제 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{applicantId}")
	@PostAuthorize("@webSecurity.checkAuthority(returnObject.memberId)")
	public void delete(@PathVariable Long applicantId) {
		applicantService.deleteApplicant(applicantId);
	}

}
