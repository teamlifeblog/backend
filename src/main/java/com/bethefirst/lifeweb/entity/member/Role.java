package com.bethefirst.lifeweb.entity.member;

import com.bethefirst.lifeweb.entity.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role implements EnumType {//권한-admin,user

    ADMIN("ROLE_ADMIN", "관리자"),//관리자
    USER("ROLE_USER", "회원");//회원

    private String value;
	private String description;

}
