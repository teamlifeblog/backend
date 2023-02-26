package com.bethefirst.lifeweb.dto.member.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConfirmationEmailDto {

	private Long memberId;
	private String code;

}
