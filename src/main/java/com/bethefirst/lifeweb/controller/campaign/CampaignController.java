package com.bethefirst.lifeweb.controller.campaign;

import com.bethefirst.lifeweb.dto.campaign.request.*;
import com.bethefirst.lifeweb.dto.campaign.response.CampaignDto;

import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import com.bethefirst.lifeweb.service.campaign.interfaces.CampaignService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("campaigns")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {

	private final CampaignService campaignService;

	/** 캠페인 생성 */
	@PostMapping
	public ResponseEntity<?> create(@Valid CreateCampaignDto createCampaignDto) {

		// 날짜 검증
		dateVerification(createCampaignDto.getApplicationStartDate(), createCampaignDto.getApplicationEndDate(),
				createCampaignDto.getFilingStartDate(), createCampaignDto.getFilingEndDate());

		// 캠페인 생성
		Long campaignId = campaignService.createCampaign(createCampaignDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/campaigns/" + campaignId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	// 날짜 검증
	private void dateVerification(LocalDate applicationStartDate, LocalDate applicationEndDate,
								  LocalDate filingStartDate, LocalDate filingEndDate) {
		if (applicationStartDate.isAfter(applicationEndDate)) {
			throw new UnprocessableEntityException("applicationEndDate", applicationEndDate,
					"신청종료일은 신청시작일 이후 날짜여야 합니다.");
		} else if (applicationEndDate.isAfter(filingStartDate)) {
			throw new UnprocessableEntityException("filingStartDate", filingStartDate,
					"등록시작일은 신청종료일 이후 날짜여야 합니다.");
		} else if (filingStartDate.isAfter(filingEndDate)) {
			throw new UnprocessableEntityException("filingEndDate", filingEndDate,
					"등록종료일은 등록시작일 이후 날짜여야 합니다.");
		}
	}

	/** 캠페인 조회 */
	@GetMapping("/{campaignId}")
	public CampaignDto read(@PathVariable Long campaignId) {
		return campaignService.getCampaignDto(campaignId);
	}

	/** 캠페인 리스트 조회 */
	@GetMapping
	@PreAuthorize("#searchRequirements.memberId == null or @webSecurity.checkAuthority(#searchRequirements.memberId)")
	public Page<CampaignDto> readAll(CampaignSearchRequirements searchRequirements,
									 @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		searchRequirements.setPageable(pageable);
		return campaignService.getCampaignDtoPage(searchRequirements);
	}

	/** 캠페인 수정 */
	@PutMapping("/{campaignId}")
	public ResponseEntity<?> update(@PathVariable Long campaignId,
									@Valid UpdateCampaignDto updateCampaignDto) {

		// 날짜 검증
		dateVerification(updateCampaignDto.getApplicationStartDate(), updateCampaignDto.getApplicationEndDate(),
				updateCampaignDto.getFilingStartDate(), updateCampaignDto.getFilingEndDate());

		// 캠페인 수정
		campaignService.updateCampaign(campaignId, updateCampaignDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/campaigns/" + campaignId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	/** 캠페인 상태 변경 */
	@PutMapping("/{campaignId}/status")
	public ResponseEntity<?> updateStatus(@PathVariable Long campaignId,
							 @Valid @RequestBody CampaignStatusDto campaignStatusDto) {

		// 캠페인 상태 변경
		campaignService.updateStatus(campaignId, campaignStatusDto.getStatus());

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/campaigns/" + campaignId);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	/** 캠페인 PICK 체크 */
	@PutMapping("/pick")
	public ResponseEntity<?> updatePick(@RequestBody UpdateCampaignPickDto updateCampaignPickDto) {

		// 캠페인 PICK 체크
		campaignService.updatePick(updateCampaignPickDto);

		// Location 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_LOCATION, "/campaigns");

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

}
