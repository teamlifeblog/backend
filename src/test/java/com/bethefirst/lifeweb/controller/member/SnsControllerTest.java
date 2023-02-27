package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.dto.member.request.CreateSnsDto;
import com.bethefirst.lifeweb.dto.member.request.UpdateSnsDto;
import com.bethefirst.lifeweb.dto.member.response.SnsDto;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.mamber.InitSnsDto;
import com.bethefirst.lifeweb.service.member.interfaces.SnsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.bethefirst.lifeweb.entity.member.Role.ADMIN;
import static com.bethefirst.lifeweb.util.RestdocsUtil.getJwt;
import static com.bethefirst.lifeweb.util.SnippetUtil.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SnsController.class)
public class SnsControllerTest extends ControllerTest {

    @MockBean SnsService snsService;

    InitSnsDto initSnsDto = new InitSnsDto();

    private String urlTemplate = "/sns";


    @Test
    @DisplayName("SNS 등록")
    void SNS_등록()throws Exception {
        CreateSnsDto dto = initSnsDto.getCreateSnsDto();

        given(snsService.createSns(dto)).willReturn(1L);

        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post(urlTemplate)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, getJwt(ADMIN, 1L))
                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
                                        headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("토큰")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("SNS 이름")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{SNS ID}")
                                )
                        )
                );

    }

    @Test
    @DisplayName("SNS 단건조회")
    void SNS_단건조회() throws Exception {
        SnsDto dto = initSnsDto.getSnsDto();
        given(snsService.getSns(1L)).willReturn(dto);

        mockMvc.perform(get(urlTemplate + "/{snsId}",1L)
                        .header(AUTHORIZATION,getJwt(ADMIN,1L))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(ADMIN)).description("토큰")
                                ),
                                pathParameters(
                                        parameterWithName("snsId").attributes(type(NUMBER)).description("SNS ID")
                                ),
                                responseFields(
                                        fieldWithPath("id").attributes(type(NUMBER)).description("SNS ID"),
                                        fieldWithPath("name").attributes(type(STRING)).description("SNS 이름")
                                )
                        )
                );
    }

    @Test
    @DisplayName("SNS 전체조회")
    void SNS_전체조회() throws Exception {
        List<SnsDto> dto = initSnsDto.getSnsDtoList();
        given(snsService.getSnsList()).willReturn(dto);

        mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                    fieldWithPath("[].id").type(NUMBER).description("SNS ID"),
                                    fieldWithPath("[].name").type(STRING).description("SNS 이름")
                                )
                        )
                );
    }

    @Test
    @DisplayName("SNS 수정")
    void SNS_수정() throws Exception {
        UpdateSnsDto dto = initSnsDto.getUpdateSnsDto();

        willDoNothing().given(snsService).updateSns(dto, 2L);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(urlTemplate + "/{snsId}",2L)
                        .header(AUTHORIZATION, getJwt(ADMIN, 1L))
                        .contentType(APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(ADMIN)).description("토큰"),
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE)
                                ),
                                pathParameters(
                                        parameterWithName("snsId").attributes(type(NUMBER)).description("SNS ID")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("SNS 이름")
                                ),responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{SNS ID}")
                                )
                        )
                );


    }

}
