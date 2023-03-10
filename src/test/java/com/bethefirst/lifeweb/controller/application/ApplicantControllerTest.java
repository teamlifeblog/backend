package com.bethefirst.lifeweb.controller.application;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.dto.application.request.CreateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantDto;
import com.bethefirst.lifeweb.dto.application.request.UpdateApplicantStatusDto;
import com.bethefirst.lifeweb.entity.application.ApplicantStatus;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.application.InitApplicantDto;
import com.bethefirst.lifeweb.service.application.interfaces.ApplicantService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.bethefirst.lifeweb.util.RestdocsUtil.*;
import static com.bethefirst.lifeweb.util.SnippetUtil.*;
import static org.mockito.BDDMockito.*;//given,willReturn
import static org.springframework.http.HttpHeaders.*;//AUTHORIZATION,CONTENT_LOCATION
import static org.springframework.http.MediaType.*;//MULTIPART_FORM_DATA,APPLICATION_JSON
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;//get,post,multipart...
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;//status
import static org.springframework.restdocs.request.RequestDocumentation.*;//pathParameters,queryParameters,requestParts
import static org.springframework.restdocs.payload.PayloadDocumentation.*;//requestFields,responseFields
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static com.bethefirst.lifeweb.util.CustomJsonFieldType.*;

@WebMvcTest(ApplicantController.class)
class ApplicantControllerTest extends ControllerTest {

	@MockBean
	ApplicantService applicationService;

	String urlTemplate = "/applicants";
	InitApplicantDto applicantDto = new InitApplicantDto();

	@Test
	void ?????????_??????() throws Exception {

		CreateApplicantDto dto = applicantDto.getCreateApplicantDto();
		given(applicationService.createApplicant(1L, dto)).willReturn(1L);

		mockMvc.perform(post(urlTemplate)
						.content(objectMapper.writeValueAsString(toMap(dto)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.USER, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.USER)).description("??????")
								),
								requestFields(
										fieldWithPath("applicationId").type(NUMBER).description("?????????ID"),
										fieldWithPath("memo").type(STRING).description("??????"),
										fieldWithPath("applicationQuestionId").type(arrayType(NUMBER)).description("???????????????ID").optional(),
										fieldWithPath("answer").type(arrayType(STRING)).description("??????").optional()
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{?????????ID}")
								)
						)
				);
	}

	@Test
	void ?????????_??????() throws Exception {

		given(applicationService.getApplicantDto(1L)).willReturn(applicantDto.getApplicantDto());

		mockMvc.perform(get(urlTemplate + "/{applicantId}", 1L)
						.header(AUTHORIZATION, getJwt(Role.USER, 1L))
				)
				.andExpect(status().isOk())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(AUTHORIZATION).attributes(info(Role.USER, Role.ADMIN)).description("??????")
								),
								pathParameters(
										parameterWithName("applicantId").attributes(type(NUMBER)).description("?????????ID")
								),
								responseFields(
										fieldWithPath("id").type(NUMBER).description("?????????ID"),
										fieldWithPath("memo").type(STRING).description("??????"),
										fieldWithPath("created").type(LOCAL_DATE_TIME).description("?????????"),
										fieldWithPath("status").type(enumType(ApplicantStatus.class)).description("??????"),
										fieldWithPath("applicantAnswerDtoList[].id").type(NUMBER).description("???????????????ID").optional(),
										fieldWithPath("applicantAnswerDtoList[].applicationQuestionId").type(NUMBER).description("???????????????ID").optional(),
										fieldWithPath("applicantAnswerDtoList[].answer").type(STRING).description("??????").optional()
								)
						)
				);
	}

	@Test
	void ?????????_?????????_??????() throws Exception {

		given(applicationService.getApplicantDtoList(applicantDto.getSearchRequirements())).willReturn(applicantDto.getApplicantDtoPage());

		mockMvc.perform(get(urlTemplate)
						.param("page", "1")
						.param("size", "10")
						.param("sort", "created,desc")
						.header(AUTHORIZATION, getJwt(Role.USER, 1L))
				)
				.andExpect(status().isOk())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(AUTHORIZATION).attributes(info(Role.USER, Role.ADMIN)).description("??????")
								),
								queryParameters(
										parameterWithName("page").attributes(type(NUMBER)).description("?????????").optional(),
										parameterWithName("size").attributes(type(NUMBER)).description("?????????").optional(),
										parameterWithName("sort").attributes(type(STRING)).description("????????????,????????????").optional(),
										parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID").optional(),
										parameterWithName("campaignId").attributes(type(NUMBER)).description("?????????ID").optional(),
										parameterWithName("status").attributes(type(enumType(ApplicantStatus.class))).description("??????").optional()
								),
								relaxedResponseFields(
										fieldWithPath("content").type(ARRAY).description("?????????"),
										fieldWithPath("content.[].id").type(NUMBER).description("?????????ID"),
										fieldWithPath("content.[].memo").type(STRING).description("??????"),
										fieldWithPath("content.[].created").type(LOCAL_DATE_TIME).description("?????????"),
										fieldWithPath("content.[].status").type(enumType(ApplicantStatus.class)).description("??????"),
										fieldWithPath("content.[].applicantAnswerDtoList[].id").type(NUMBER).description("???????????????ID").optional(),
										fieldWithPath("content.[].applicantAnswerDtoList[].applicationQuestionId").type(NUMBER).description("??????ID").optional(),
										fieldWithPath("content.[].applicantAnswerDtoList[].answer").type(STRING).description("??????").optional(),
										fieldWithPath("pageable").type(Pageable.class.getSimpleName()).description("?????????"),
										fieldWithPath("last").type(BOOLEAN).description("??????????????????"),
										fieldWithPath("totalPages").type(NUMBER).description("???????????????"),
										fieldWithPath("totalElements").type(NUMBER).description("??????????????????"),
										fieldWithPath("size").type(NUMBER).description("?????????"),
										fieldWithPath("number").type(NUMBER).description("?????????"),
										fieldWithPath("sort").type(Sort.class.getSimpleName()).description("??????"),
										fieldWithPath("first").type(BOOLEAN).description("????????????"),
										fieldWithPath("numberOfElements").type(NUMBER).description("????????????"),
										fieldWithPath("empty").type(BOOLEAN).description("empty")
								)
						)
				);
	}

	@Test
	void ?????????_??????() throws Exception {

		UpdateApplicantDto dto = applicantDto.getUpdateApplicantDto();
		willDoNothing().given(applicationService).updateApplicant(1L, dto);

		mockMvc.perform(put(urlTemplate + "/{applicantId}", 1L)
						.content(objectMapper.writeValueAsString(toMap(dto)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.USER, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.USER)).description("??????")
								),
								pathParameters(
										parameterWithName("applicantId").attributes(type(NUMBER)).description("?????????ID")
								),
								requestFields(
										fieldWithPath("memo").type(STRING).description("??????"),
										fieldWithPath("applicantAnswerId").type(arrayType(NUMBER)).description("???????????????ID").optional(),
										fieldWithPath("answer").type(arrayType(STRING)).description("??????").optional()
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{?????????ID}")
								)
						)
				);
	}

	@Test
	void ?????????_??????_??????() throws Exception {

		UpdateApplicantStatusDto dto = applicantDto.getUpdateApplicantStatusDto();
		willDoNothing().given(applicationService).updateStatus(dto);

		mockMvc.perform(put(urlTemplate + "/status")
						.content(objectMapper.writeValueAsString(toMap(dto)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("??????")
								),
								requestFields(
										fieldWithPath("campaignId").type(NUMBER).description("?????????ID"),
										fieldWithPath("selectApplicantId").type(arrayType(NUMBER)).description("????????? ?????????ID"),
										fieldWithPath("unselectApplicantId").type(arrayType(NUMBER)).description("???????????? ?????????ID")
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).description(urlTemplate)
								)
						)
				);
	}

	@Test
	void ?????????_??????() throws Exception {

		willDoNothing().given(applicationService).deleteApplicant(1L);

		mockMvc.perform(delete(urlTemplate + "/{applicantId}", 1L)
						.header(AUTHORIZATION, getJwt(Role.USER, 1L))
				)
				.andExpect(status().isNoContent())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(AUTHORIZATION).attributes(info(Role.USER, Role.ADMIN)).description("??????")
								),
								pathParameters(
										parameterWithName("applicantId").attributes(type(NUMBER)).description("?????????ID")
								)
						)
				);
	}

}