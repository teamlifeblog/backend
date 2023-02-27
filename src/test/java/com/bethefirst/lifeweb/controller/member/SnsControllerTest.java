package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.dto.member.request.CreateSnsDto;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.mamber.InitSnsDto;
import com.bethefirst.lifeweb.service.member.interfaces.SnsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.bethefirst.lifeweb.entity.member.Role.ADMIN;
import static com.bethefirst.lifeweb.util.RestdocsUtil.getJwt;
import static com.bethefirst.lifeweb.util.SnippetUtil.info;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SnsController.class)
public class SnsControllerTest extends ControllerTest {

    @MockBean SnsService snsService;

    InitSnsDto initSnsDto = new InitSnsDto();

    private String urlTemplate = "/sns";


    @Test
    @DisplayName("SNS 등록")
    void SNS_등록()throws Exception{
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


}
