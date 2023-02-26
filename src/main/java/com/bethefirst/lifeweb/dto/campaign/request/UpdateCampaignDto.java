package com.bethefirst.lifeweb.dto.campaign.request;

import com.bethefirst.lifeweb.dto.application.response.ApplicationQuestionDto;
import com.bethefirst.lifeweb.dto.campaign.response.CampaignLocalDto;
import com.bethefirst.lifeweb.entity.application.QuestionType;
import com.bethefirst.lifeweb.entity.campaign.*;
import com.bethefirst.lifeweb.entity.member.Sns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignDto {

	@NotNull(message = "카테고리는 필수 입력 값입니다.")
	private Long categoryId;//카테고리

	@NotNull(message = "타입은 필수 입력 값입니다.")
	private Long typeId;//타입

	@NotNull(message = "SNS는 필수 입력 값입니다.")
	private Long snsId;//SNS


	@NotNull(message = "스페셜은 필수 입력 값입니다.")
	private Boolean special;//스페셜

	@NotBlank(message = "제목은 필수 입력 값입니다.")
	private String title;//제목

	private MultipartFile uploadFile;//대표이미지 파일
	private String fileName;//대표이미지

	@NotBlank(message = "제공내역은 필수 입력 값입니다.")
	private String provision;//제공내역

	@NotBlank(message = "리뷰주의사항은 필수 입력 값입니다.")
	private String reviewNotice;//리뷰주의사항

	@NotBlank(message = "가이드라인은 필수 입력 값입니다.")
	private String guideline;//가이드라인

	@NotBlank(message = "키워드는 필수 입력 값입니다.")
	private String keywords;//키워드

	@NotNull(message = "신청시작일은 필수 입력 값입니다.")
	private LocalDate applicationStartDate;//신청시작일

	@NotNull(message = "신청종료일은 필수 입력 값입니다.")
	private LocalDate applicationEndDate;//신청종료일

	@NotNull(message = "등록시작일은 필수 입력 값입니다.")
	private LocalDate filingStartDate;//등록시작일

	@NotNull(message = "등록종료일은 필수 입력 값입니다.")
	private LocalDate filingEndDate;//등록종료일

	@NotNull(message = "모집인원은 필수 입력 값입니다.")
	private Integer headcount;//모집인원

	private Long localId;//지역

	private String address;//주소
	private String latitude;//위도
	private String longitude;//경도
	private String visitNotice;//방문주의사항


	private List<Long> campaignImageId = new ArrayList<>();//기존 이미지 ID
	private List<MultipartFile> uploadFileList = new ArrayList<>();//새로운 이미지 파일

	private List<Long> applicationQuestionId = new ArrayList<>();//질문ID
	private List<String> question;//질문
	private List<QuestionType> type;//유형
	private List<String> items;//항목

	public CampaignLocalDto getCampaignLocalDto() {
		return new CampaignLocalDto(localId, address, latitude, longitude, visitNotice);
	}
	public List<ApplicationQuestionDto> getApplicationQuestionDtoList() {
		List<ApplicationQuestionDto> list = new ArrayList<>();
		for (int i = 0; i < applicationQuestionId.size(); i++) {
			list.add(new ApplicationQuestionDto(applicationQuestionId.get(i), question.get(i), type.get(i),
					type.get(i) == QuestionType.RADIO || type.get(i) == QuestionType.CHECKBOX ? items.get(i) : null));
		}
		return list;
	}

	/** 캠페인 수정 */
	public void updateCampaign(Campaign campaign, CampaignCategory campaignCategory, CampaignType campaignType, Sns sns) {
		campaign.updateCampaign(campaignCategory, campaignType, sns,
				special, title, fileName, provision,
				reviewNotice, guideline, keywords,
				applicationStartDate, applicationEndDate,
				filingStartDate, filingEndDate, headcount);
	}

}
