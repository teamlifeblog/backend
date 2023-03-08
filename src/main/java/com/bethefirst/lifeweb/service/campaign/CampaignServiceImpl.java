package com.bethefirst.lifeweb.service.campaign;

import com.bethefirst.lifeweb.dto.UploadFile;
import com.bethefirst.lifeweb.dto.campaign.request.CampaignSearchRequirements;
import com.bethefirst.lifeweb.dto.campaign.request.CreateCampaignDto;
import com.bethefirst.lifeweb.dto.campaign.request.UpdateCampaignDto;
import com.bethefirst.lifeweb.dto.campaign.request.UpdateCampaignPickDto;
import com.bethefirst.lifeweb.dto.campaign.response.CampaignDto;
import com.bethefirst.lifeweb.entity.campaign.*;
import com.bethefirst.lifeweb.entity.member.Sns;
import com.bethefirst.lifeweb.exception.ConflictException;
import com.bethefirst.lifeweb.repository.campaign.*;
import com.bethefirst.lifeweb.repository.member.SnsRepository;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicationService;
import com.bethefirst.lifeweb.service.campaign.interfaces.CampaignService;
import com.bethefirst.lifeweb.util.AwsS3Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class CampaignServiceImpl implements CampaignService {

	private final ApplicationService applicationService;
	private final CampaignRepository campaignRepository;
	private final CampaignLocalRepository campaignLocalRepository;
	private final CampaignCategoryRepository campaignCategoryRepository;
	private final CampaignTypeRepository campaignTypeRepository;
	private final LocalRepository localRepository;
	private final CampaignImageRepository campaignImageRepository;
	private final SnsRepository snsRepository;
	private final AwsS3Util awsS3Util;

	@Value("${image-folder.campaign}")
	private String imageFolder;

	/** 캠페인 생성 */
	@Override
	public Long createCampaign(CreateCampaignDto createCampaignDto) {

		// 캠페인 저장
		CampaignCategory campaignCategory = campaignCategoryRepository.findById(createCampaignDto.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다. " + createCampaignDto.getCategoryId()));
		CampaignType campaignType = campaignTypeRepository.findById(createCampaignDto.getTypeId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 타입입니다. " + createCampaignDto.getTypeId()));
		Sns sns = snsRepository.findById(createCampaignDto.getSnsId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 SNS입니다. " + createCampaignDto.getSnsId()));

		// 대표이미지 파일 이름 설정
		UploadFile uploadFile = new UploadFile(createCampaignDto.getUploadFile(), imageFolder);
		createCampaignDto.setFileName(uploadFile.getKey());

		Campaign campaign = createCampaignDto.createCampaign(campaignCategory, campaignType, sns);

		Long campaignId = campaignRepository.save(campaign).getId();

		// 캠페인지역 저장
		if (createCampaignDto.getLocalId() != null) {
			Local local = localRepository.findById(createCampaignDto.getLocalId())
					.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다. " + createCampaignDto.getLocalId()));

			campaignLocalRepository.save(createCampaignDto.getCampaignLocalDto().createCampaignLocal(campaign, local));
		}

		// 캠페인이미지 파일 리스트 이름 설정
		List<UploadFile> uploadFileList = UploadFile.getList(createCampaignDto.getUploadFileList(), imageFolder);
		// 캠페인이미지 저장
		uploadFileList.forEach(uf -> campaignImageRepository.save(new CampaignImage(campaign, uf.getKey())));

		// 신청서, 신청서질문 저장
		if (!createCampaignDto.getApplicationQuestionDtoList().isEmpty()) {
			applicationService.createApplication(campaign, createCampaignDto.getApplicationQuestionDtoList());
		}

		// 이미지 파일 저장
		uploadFileList.add(uploadFile);
		awsS3Util.upload(uploadFileList);

		return campaignId;
	}

	/** 캠페인 조회 */
	@Transactional(readOnly = true)
	@Override
	public CampaignDto getCampaignDto(Long campaignId) {
		return new CampaignDto(campaignRepository.findById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + campaignId)));
	}

	/** 캠페인 리스트 조회 */
	@Transactional(readOnly = true)
	@Override
	public Page<CampaignDto> getCampaignDtoPage(CampaignSearchRequirements searchRequirements) {
		return campaignRepository.findAllBySearchRequirements(searchRequirements).map(CampaignDto::new);
	}

	/** 캠페인 수정 */
	@Override
	public void updateCampaign(Long campaignId, UpdateCampaignDto updateCampaignDto) {

		// 캠페인 수정
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + campaignId));

		// 상태가 대기 일때만 수정 가능
		if(!campaign.getStatus().equals(CampaignStatus.STAND)) {
			throw new ConflictException("대기상태에서만 캠페인 수정이 가능합니다.");
		}

		CampaignCategory campaignCategory = campaignCategoryRepository.findById(updateCampaignDto.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다. " + updateCampaignDto.getCategoryId()));
		CampaignType campaignType = campaignTypeRepository.findById(updateCampaignDto.getTypeId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 타입입니다. " + updateCampaignDto.getTypeId()));
		Sns sns = snsRepository.findById(updateCampaignDto.getSnsId())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 SNS입니다. " + updateCampaignDto.getSnsId()));

		//대표이미지 파일 이름 설정
		UploadFile uploadFile = null;
		String oldFileName = null;
		if (updateCampaignDto.getUploadFile() != null) {
			//저장할 이미지
			uploadFile = new UploadFile(updateCampaignDto.getUploadFile(), imageFolder);
			updateCampaignDto.setFileName(uploadFile.getKey());
			//삭제할 이미지
			oldFileName = campaign.getFileName();
		}

		updateCampaignDto.updateCampaign(campaign, campaignCategory, campaignType, sns);

		// 캠페인지역 수정
		if (updateCampaignDto.getLocalId() != null) {
			Local local = localRepository.findById(updateCampaignDto.getLocalId())
					.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다. " + updateCampaignDto.getLocalId()));
			//캠페인지역 insert
			if (campaign.getCampaignLocal() == null) {
				campaignLocalRepository.save(updateCampaignDto.getCampaignLocalDto().createCampaignLocal(campaign, local));
			//캠페인지역 update
			} else {
				CampaignLocal campaignLocal = campaign.getCampaignLocal();
				updateCampaignDto.getCampaignLocalDto().updateCampaignLocal(campaignLocal, local);
			}
		}

		// 캠페인이미지 수정

		//캠페인이미지 저장
		//이미지 파일 리스트 이름 설정
		List<UploadFile> uploadFileList = UploadFile.getList(updateCampaignDto.getUploadFileList(), imageFolder);
		//캠페인이미지 insert
		uploadFileList.forEach(uf -> campaignImageRepository.save(new CampaignImage(campaign, uf.getKey())));

		//캠페인이미지 삭제
		List<String> deleteFileList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(campaign.getCampaignImageList())) {
			campaign.getCampaignImageList().stream().filter(campaignImage -> {
				for (Long campaignImageId : updateCampaignDto.getCampaignImageId()) {
					if (campaignImage.getId().equals(campaignImageId)) return false;
				}
				return true;
			}).forEach(campaignImage -> {
				//삭제할 이미지 리스트
				deleteFileList.add(campaignImage.getFileName());
				//캠페인이미지 delete
				campaignImageRepository.delete(campaignImage);
			});
		}

		// 신청서질문 수정
		applicationService.updateApplication(campaign.getApplication(), updateCampaignDto.getApplicationQuestionDtoList());


		//이미지 파일 저장, 삭제
		if (uploadFile != null) {
			uploadFileList.add(uploadFile);
			deleteFileList.add(oldFileName);
		}
		// 이미지 파일 저장
		awsS3Util.upload(uploadFileList);
		// 이미지 파일 삭제
		awsS3Util.delete(deleteFileList);

	}

	/** 캠페인 상태 변경 */
	@Override
	public void updateStatus(Long campaignId, CampaignStatus status) {
		campaignRepository.findById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 캠페인입니다. " + campaignId))
				.updateCampaignStatus(status);
	}

	/** 캠페인 PICK 체크 */
	@Override
	public void updatePick(UpdateCampaignPickDto updateCampaignPickDto) {

		campaignRepository.updatePick(true, updateCampaignPickDto.getNewCampaignId());
		campaignRepository.updatePick(false, updateCampaignPickDto.getOldCampaignId());

		if (campaignRepository.countByPick(true) > 10) {
			throw new ConflictException("PICK은 10개까지만 선택가능합니다.");
		}

	}

}
