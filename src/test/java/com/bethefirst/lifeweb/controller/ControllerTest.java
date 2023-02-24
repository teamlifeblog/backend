package com.bethefirst.lifeweb.controller;

import com.bethefirst.lifeweb.util.RestDocsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@Slf4j
public abstract class ControllerTest {

	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected RestDocumentationResultHandler restDocs;
	@Autowired
	protected ObjectMapper objectMapper;

	@BeforeEach
	void setUp(final WebApplicationContext context,
			   final RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(provider)) // rest docs 설정 주입
				.alwaysDo(MockMvcResultHandlers.print()) // andDo(print()) 코드 포함
				.alwaysDo(restDocs) // pretty 패턴과 문서 디렉토리 명 정해준것 적용
				.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
				.build();
	}

}
