package com.bethefirst.lifeweb.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmationEmailDto {

	private Long memberId;
	private String code;

}
