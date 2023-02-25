package com.bethefirst.lifeweb.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MemberSns {//SNS주소

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_sns_id")
	private Long id;//SNS주소ID PK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;//회원번호 FK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_id")
	private Sns sns;//SNS FK

	private String url;//SNS주소

	public MemberSns(Member member, Sns sns, String url) {
		this.member = member;
		this.sns = sns;
		this.url = url;

	}

	public void updateMemberSns(String snsUrl) {
		this.url = snsUrl;
	}

}
