package com.bethefirst.lifeweb.controller.campaign;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.campaign.InitCampaignCategoryDto;
import com.bethefirst.lifeweb.service.campaign.interfaces.CampaignCategoryService;
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

@WebMvcTest(CampaignCategoryController.class)
class CampaignCategoryControllerTest extends ControllerTest {

	@MockBean
	CampaignCategoryService campaignCategoryService;

	String urlTemplate = "/campaign-categories";
	InitCampaignCategoryDto campaignCategoryDto = new InitCampaignCategoryDto();

	@Test
	void 캠페인카테고리_생성() throws Exception {

		willDoNothing().given(campaignCategoryService).createCampaignCategory("새 카테고리이름");

		mockMvc.perform(post(urlTemplate)
						.content(objectMapper.writeValueAsString(Map.of(
								"campaignCategoryName", "새 카테고리이름"
						)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("token")
								),
								requestFields(
										fieldWithPath("campaignCategoryName").attributes(type(STRING)).description("카테고리이름")
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate)).description(CONTENT_LOCATION)
								)
						)
				);
	}

	@Test
	void 캠페인카테고리_리스트_조회() throws Exception {

		given(campaignCategoryService.getCampaignCategoryDtoList()).willReturn(campaignCategoryDto.getCampaignCategoryDtoList());

		mockMvc.perform(get(urlTemplate))
				.andExpect(status().isOk())
				.andDo(
						restDocs.document(
								responseFields(
										fieldWithPath("content.[].id").type(NUMBER).description("카테고리ID"),
										fieldWithPath("content.[].name").type(STRING).description("카테고리이름")
								)
						)
				);
	}

	@Test
	void 캠페인카테고리_수정() throws Exception {

		willDoNothing().given(campaignCategoryService).updateCampaignCategory(1L, "수정된 카테고리이름");

		mockMvc.perform(put(urlTemplate + "/{campaignCategoryId}", 1L)
						.content(objectMapper.writeValueAsString(Map.of(
								"campaignCategoryName", "수정된 카테고리이름"
						)))
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isCreated())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("token")
								),
								pathParameters(
										parameterWithName("campaignCategoryId").attributes(type(NUMBER)).description("카테고리ID")
								),
								requestFields(
										fieldWithPath("campaignCategoryName").type(STRING).description("카테고리이름")
								),
								responseHeaders(
										headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate)).description(CONTENT_LOCATION)
								)
						)
				);
	}

	@Test
	void 캠페인카테고리_삭제() throws Exception {

		willDoNothing().given(campaignCategoryService).deleteCampaignCategory(1L);

		mockMvc.perform(delete(urlTemplate + "/{campaignCategoryId}", 1L)
						.header(AUTHORIZATION, getJwt(Role.ADMIN, 1L))
				)
				.andExpect(status().isNoContent())
				.andDo(
						restDocs.document(
								requestHeaders(
										headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("token")
								),
								pathParameters(
										parameterWithName("campaignCategoryId").attributes(type(NUMBER)).description("카테고리ID")
								)
						)
				);
	}

}