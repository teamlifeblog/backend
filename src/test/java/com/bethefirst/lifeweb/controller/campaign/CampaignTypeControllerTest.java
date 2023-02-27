package com.bethefirst.lifeweb.controller.campaign;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.campaign.InitCampaignTypeDto;
import com.bethefirst.lifeweb.service.campaign.interfaces.CampaignTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

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

@WebMvcTest(CampaignTypeController.class)
class CampaignTypeControllerTest extends ControllerTest {

	@MockBean
	CampaignTypeService campaignTypeService;

	String urlTemplate = "/campaign-types";
	InitCampaignTypeDto campaignTypeDto = new InitCampaignTypeDto();

	@Test
	void 캠페인타입_생성() throws Exception {

		willDoNothing().given(campaignTypeService).createCampaignType("새 타입이름");

		mockMvc.perform(post(urlTemplate)
						.content(objectMapper.writeValueAsString(Map.of(
								"campaignTypeName", "새 타입이름"
						)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("토큰")
								),
								requestFields(
										fieldWithPath("campaignTypeName").type(STRING).description("타입이름")
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).description(urlTemplate)
								)
						)
				);
	}

	@Test
	void 캠페인타입_리스트_조회() throws Exception {

		given(campaignTypeService.getCampaignTypeDtoList()).willReturn(campaignTypeDto.getCampaignTypeDtoList());

		mockMvc.perform(get(urlTemplate))
				.andExpect(status().isOk())
				.andDo(
						restDocs.document(
								responseFields(
										fieldWithPath("content.[].id").type(NUMBER).description("타입ID"),
										fieldWithPath("content.[].name").type(STRING).description("타입이름")
								)
						)
				);
	}

	@Test
	void 캠페인타입_수정() throws Exception {

		willDoNothing().given(campaignTypeService).updateCampaignType(1L, "수정된 타입이름");

		mockMvc.perform(put(urlTemplate + "/{campaignTypeId}", 1L)
						.content(objectMapper.writeValueAsString(Map.of(
								"campaignTypeName", "수정된 타입이름"
						)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("토큰")
								),
								pathParameters(
										parameterWithName("campaignTypeId").attributes(type(NUMBER)).description("타입ID")
								),
								requestFields(
										fieldWithPath("campaignTypeName").type(STRING).description("타입이름")
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).description(urlTemplate)
								)
						)
				);
	}

	@Test
	void 캠페인타입_삭제() throws Exception {

		willDoNothing().given(campaignTypeService).deleteCampaignType(1L);

		mockMvc.perform(delete(urlTemplate + "/{campaignTypeId}", 1L)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isNoContent())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("토큰")
								),
								pathParameters(
										parameterWithName("campaignTypeId").attributes(type(NUMBER)).description("타입ID")
								)
						)
				);
	}

}