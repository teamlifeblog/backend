package com.bethefirst.lifeweb.dto.member.response;

import com.bethefirst.lifeweb.entity.member.Member;
import com.bethefirst.lifeweb.entity.member.MemberSns;
import com.bethefirst.lifeweb.entity.member.Sns;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSnsDto {

	private Long memberSnsId; //회원 SNS PK
	private Long snsId; //SNS PK
	private String snsName; //SNS 이름
	private String url;//SNS주소

	public MemberSnsDto(MemberSns memberSns) {
		this.memberSnsId = memberSns.getId();
		this.snsId = memberSns.getSns().getId();
		this.snsName = memberSns.getSns().getName();
		this.url = memberSns.getUrl();
	}

	public MemberSnsDto(Long memberSnsId, Long snsId, String url) {
		this.memberSnsId = memberSnsId;
		this.snsId = snsId;
		this.url = url;
	}

	public MemberSns createMemberSns(Member member, Sns sns) {
		return new MemberSns(member, sns, url);
	}

	public void updateMemberSns(MemberSns memberSns) {
		memberSns.updateMemberSns(url);
	}

}
