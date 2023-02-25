package com.bethefirst.lifeweb.service.member.interfaces;

import com.bethefirst.lifeweb.dto.member.response.MemberSnsDto;
import com.bethefirst.lifeweb.entity.member.Member;

import java.util.List;

public interface MemberSnsService {

	/** 회원 SNS 수정 */
	void updateMemberSns(Member member, List<MemberSnsDto> memberSnsDtoList);
	
}
