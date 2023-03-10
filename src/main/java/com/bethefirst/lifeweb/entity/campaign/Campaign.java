package com.bethefirst.lifeweb.entity.campaign;

import com.bethefirst.lifeweb.entity.application.Application;
import com.bethefirst.lifeweb.entity.member.Sns;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EntityListeners(AuditingEntityListener.class)
public class Campaign {//캠페인

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "campaign_id")
	private Long id;//캠페인ID PK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_category_id")
	private CampaignCategory campaignCategory;//캠페인카테고리 FK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_type_id")
	private CampaignType campaignType;//캠페인타입 FK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_id")
	private Sns sns;//SNS FK

	private Boolean special;//스페셜
	private Boolean pick;//픽
	private String title;//제목
	private String fileName;//대표이미지
	private String provision;//제공내역

//	@CreatedDate
	private LocalDateTime created;//등록일

	private String reviewNotice;//리뷰주의사항
	private String guideline;//가이드라인
	private String keywords;//키워드
	private LocalDate applicationStartDate;//신청시작일
	private LocalDate applicationEndDate;//신청종료일
	private LocalDate filingStartDate;//등록시작일
	private LocalDate filingEndDate;//등록종료일

	private Integer headcount;//모집인원

	@Enumerated(EnumType.STRING)
	private CampaignStatus status;//상태


	@OneToOne(mappedBy = "campaign")
	private CampaignLocal campaignLocal;//캠페인지역

	@OneToMany(mappedBy = "campaign")
	private List<CampaignImage> campaignImageList = new ArrayList<>();//캠페인이미지

	@OneToOne(mappedBy = "campaign")
	private Application application;//신청서


	public Campaign(CampaignCategory campaignCategory, CampaignType campaignType, Sns sns,
					 Boolean special, String title, String fileName, String provision,
					 String reviewNotice, String guideline, String keywords,
					 LocalDate applicationStartDate, LocalDate applicationEndDate,
					 LocalDate filingStartDate, LocalDate filingEndDate, Integer headcount) {

		this.campaignCategory = campaignCategory;
		this.campaignType = campaignType;
		this.sns = sns;

		this.special = special;
		this.title = title;
		this.pick = false;
		this.fileName = fileName;
		this.provision = provision;
		this.created = LocalDateTime.now();
		this.reviewNotice = reviewNotice;
		this.guideline = guideline;
		this.keywords = keywords;
		this.applicationStartDate = applicationStartDate;
		this.applicationEndDate = applicationEndDate;
		this.filingStartDate = filingStartDate;
		this.filingEndDate = filingEndDate;
		this.headcount = headcount;
		this.status = CampaignStatus.STAND;

	}

	/** 캠페인 수정 */
	public void updateCampaign(CampaignCategory campaignCategory, CampaignType campaignType, Sns sns,
							   Boolean special, String title, String fileName, String provision,
							   String reviewNotice, String guideline, String keywords,
							   LocalDate applicationStartDate, LocalDate applicationEndDate,
							   LocalDate filingStartDate, LocalDate filingEndDate, Integer headcount) {

		this.campaignCategory = campaignCategory;
		this.campaignType = campaignType;
		this.sns = sns;

		this.special = special;
		this.title = title;
		this.fileName = fileName == null ? this.fileName : fileName;
		this.provision = provision;
		this.reviewNotice = reviewNotice;
		this.guideline = guideline;
		this.keywords = keywords;
		this.applicationStartDate = applicationStartDate;
		this.applicationEndDate = applicationEndDate;
		this.filingStartDate = filingStartDate;
		this.filingEndDate = filingEndDate;
		this.headcount = headcount;

	}

	/** 캠페인 상태 변경 */
	public void updateCampaignStatus(CampaignStatus status) {
		this.status = status;
	}

}
