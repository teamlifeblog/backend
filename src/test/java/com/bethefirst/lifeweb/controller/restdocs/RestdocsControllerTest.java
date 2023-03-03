package com.bethefirst.lifeweb.controller.restdocs;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.util.EnumResponseFieldsSnippet;
import com.bethefirst.lifeweb.util.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;//get,post,multipart...
import static org.springframework.restdocs.snippet.Attributes.*;//attributes,key
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;//status
import static org.springframework.restdocs.payload.PayloadDocumentation.*;//beneathPath,fieldWithPath

@WebMvcTest(RestdocsController.class)
class RestdocsControllerTest extends ControllerTest {

	private String urlTemplate = "/restdocs";

	@Test
	void Enum_리스트_조회() throws Exception {

		ResultActions resultActions = mockMvc.perform(get(urlTemplate + "/enums"))
				.andExpect(status().isOk());

		resultActions.andDo(
				restDocs.document(
						enumResponseFields(getData(resultActions.andReturn()))
				)
		);

	}

	@Test
	void 에러() throws Exception {

		mockMvc.perform(get(urlTemplate + "/errors"))
				.andExpect(status().is4xxClientError())
				.andDo(
						restDocs.document(
								responseFields(
										fieldWithPath("status").description("HttpStatus 코드"),
										fieldWithPath("error").description("HttpStatus 이름"),
										fieldWithPath("message").description("에러 메세지"),
										fieldWithPath("path").description("요청 경로"),
										fieldWithPath("errors").description("에러 필드").optional(),
										fieldWithPath("errors.[].field").description("필드명").optional(),
										fieldWithPath("errors.[].value").description("필드값").optional(),
										fieldWithPath("errors.[].message").description("에러 메세지").optional()
								)
						)
				)
		;
	}

	private EnumResponseFieldsSnippet[] enumResponseFields(Map<String, Map<String, String>> mvcResult) {
		return mvcResult.entrySet().stream()
				.map(entry -> new EnumResponseFieldsSnippet("enum-response",
					beneathPath(entry.getKey()).withSubsectionId(entry.getKey()),
					enumConvertFieldDescriptor(entry.getValue()),
					attributes(key("title").value(StringUtil.convertKebabToPascal(entry.getKey()))),
					true)
		).toArray(value -> new EnumResponseFieldsSnippet[value]);
	}

	private List<FieldDescriptor> enumConvertFieldDescriptor(Map<String, String> enumValues) {
		return enumValues.entrySet().stream()
				.map(entry -> fieldWithPath(entry.getKey()).description(entry.getValue()))
				.collect(Collectors.toList());
	}

	private Map<String, Map<String, String>> getData(MvcResult result) throws IOException {
		return objectMapper.readValue(result.getResponse().getContentAsByteArray(), new TypeReference<>() {});
	}

}