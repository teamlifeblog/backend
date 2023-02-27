package com.bethefirst.lifeweb.service.member;

import com.bethefirst.lifeweb.dto.member.response.MemberSnsDto;
import com.bethefirst.lifeweb.entity.member.Member;
import com.bethefirst.lifeweb.entity.member.MemberSns;
import com.bethefirst.lifeweb.entity.member.Sns;
import com.bethefirst.lifeweb.repository.member.MemberSnsRepository;
import com.bethefirst.lifeweb.repository.member.SnsRepository;
import com.bethefirst.lifeweb.service.member.interfaces.MemberSnsService;
import com.bethefirst.lifeweb.util.UrlUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberSnsServiceImpl implements MemberSnsService {

    private final MemberSnsRepository memberSnsRepository;
    private final SnsRepository snsRepository;
    private final UrlUtil urlUtil;

	/** 회원 SNS 수정 */
	@Override
	public void updateMemberSns(Member member, List<MemberSnsDto> memberSnsDtoList) {

		// 회원 SNS insert
		if (!CollectionUtils.isEmpty(memberSnsDtoList)) {
			memberSnsDtoList.stream().filter(memberSnsDto -> memberSnsDto.getMemberSnsId() == 0)
					.forEach(memberSnsDto -> {
						// url 검사
						inspectionUrl(memberSnsDto.getUrl());
						
						Sns sns = snsRepository.findById(memberSnsDto.getSnsId())
								.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 SNS 입니다. " + memberSnsDto.getSnsId()));
						// insert
						memberSnsRepository.save(memberSnsDto.createMemberSns(member, sns));
					});
		}

		if (!CollectionUtils.isEmpty(member.getMemberSnsList())) {
			for (MemberSns memberSns : member.getMemberSnsList()) {
				boolean result = false;
				if (!CollectionUtils.isEmpty(memberSnsDtoList)) {
					for (MemberSnsDto memberSnsDto : memberSnsDtoList) {
						// 회원 SNS update
						if (memberSnsDto.getMemberSnsId().equals(memberSns.getId())) {
							result = true;
							if (!memberSnsDto.getUrl().equals(memberSns.getUrl())) {
								// url 검사
								inspectionUrl(memberSnsDto.getUrl());
								// update
								memberSnsDto.updateMemberSns(memberSns);
							}
							break;
						}
					}
				}
				// 회원 SNS delete
				if (!result) {
					memberSnsRepository.delete(memberSns);
				}
			}
		}
	}

	// url 검사
	private void inspectionUrl(String url) {

		// url 유효성 검사
		urlUtil.inspectionUrl(url);

		// SNS 중복 검사
		if (memberSnsRepository.existsByUrl(url)) {
			throw new IllegalArgumentException("이미 등록되어 있는 URL 입니다. " + url);
		}
		
	}

}
