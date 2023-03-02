package com.bethefirst.lifeweb.controller.restdocs;

import com.bethefirst.lifeweb.entity.EnumType;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import com.bethefirst.lifeweb.entity.application.QuestionType;
import com.bethefirst.lifeweb.entity.campaign.CampaignStatus;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.exception.UnprocessableEntityException;
import com.bethefirst.lifeweb.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/restdocs")
@RequiredArgsConstructor
@Slf4j
public class RestdocsController {

	@GetMapping("/enums")
	public Map<String, Map<String, String>> readAll() {
		log.info("/enums");
		return toMap(Role.class, CampaignStatus.class, ApplicantStatus.class, QuestionType.class);
	}

	@GetMapping("/errors")
	public void error() {
		throw new UnprocessableEntityException("필드명", "필드값", "에러 메세지");
	}

	private Map<String, Map<String, String>> toMap(Class<? extends EnumType>... enumList) {

		Map<String, Map<String, String>> map = new HashMap();

		for (Class<? extends EnumType> enumTypeClass : enumList) {

			Map<String, String> enumTypeMap = new LinkedHashMap<>();

			for (EnumType enumType : enumTypeClass.getEnumConstants()) {
				enumTypeMap.put(enumType.name(), enumType.getDescription());
			}
			map.put(StringUtil.convertPascalToKebab(enumTypeClass.getSimpleName()), enumTypeMap);
		}

		return map;

//		return Arrays.stream(enumList).collect(Collectors
//						.toMap(enumTypes -> StringUtil.convertPascalToKebab(enumTypes.getSimpleName()),
//								enumTypes -> Arrays.stream(enumTypes.getEnumConstants()).collect(Collectors
//										.toMap(EnumType::name, EnumType::getDescription, (s1, s2) -> s1, LinkedHashMap::new)
//								)
//						)
//		);

	}

}
