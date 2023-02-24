package com.bethefirst.lifeweb.entity.application;

import com.bethefirst.lifeweb.entity.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicantStatus implements EnumType {//상태-select,unselect

	SELECT("선정"),//선정
	UNSELECT("미선정");//미선정

	private String description;

}
