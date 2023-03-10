package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.dto.member.request.LoginDto;
import com.bethefirst.lifeweb.dto.member.request.UpdateMemberDto;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.mamber.InitMemberDto;
import com.bethefirst.lifeweb.service.member.interfaces.MemberService;
import com.bethefirst.lifeweb.service.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.bethefirst.lifeweb.entity.member.Role.*;
import static com.bethefirst.lifeweb.util.CustomJsonFieldType.*;
import static com.bethefirst.lifeweb.util.CustomRestDocumentationRequestBuilders.multipart;
import static com.bethefirst.lifeweb.util.RestdocsUtil.*;
import static com.bethefirst.lifeweb.util.SnippetUtil.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MemberController.class)
public class MemberControllerTest extends ControllerTest {

    private String urlTemplate = "/members";
    InitMemberDto initMemberDto = new InitMemberDto();

    @MockBean MemberService memberService;

    @MockBean CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("?????? ??????")
    void ??????_??????() throws Exception{

        willDoNothing().given(memberService).join(initMemberDto.getJoinDto());

        String json = objectMapper.writeValueAsString(initMemberDto.getJoinDto());

        mockMvc.perform(post(urlTemplate)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE)
                                ),
                                requestFields(
                                        fieldWithPath("email").type(STRING).description("?????????"),
                                        fieldWithPath("pwd").type(STRING).description("????????????"),
                                        fieldWithPath("confirmPwd").type(STRING).description("???????????? ??????"),
                                        fieldWithPath("nickname").type(STRING).description("?????????"),
                                        fieldWithPath("name").type(STRING).description("??????"),
                                        fieldWithPath("gender").type(STRING).description("??????"),
                                        fieldWithPath("birth").type(LOCAL_DATE).description("??????"),
                                        fieldWithPath("tel").type(STRING).description("???????????????"),
                                        fieldWithPath("postcode").type(STRING).description("????????????"),
                                        fieldWithPath("address").type(STRING).description("??????"),
                                        fieldWithPath("detailAddress").type(STRING).description("????????????").optional(),
                                        fieldWithPath("extraAddress").type(STRING).description("????????????")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/login")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ?????????")
    void ??????_?????????() throws Exception{
        LoginDto loginDto = initMemberDto.getLoginDto();

        String jwt = getJwt(USER, 1L);
        given(userDetailsService.login(loginDto)).willReturn(jwt);
        String json = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(post(urlTemplate + "/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE)
                                ),
                                requestFields(
                                        fieldWithPath("email").attributes(type(STRING)).description("?????????"),
                                        fieldWithPath("pwd").attributes(type(STRING)).description("????????????")
                                ),
                                responseHeaders(
                                        headerWithName(AUTHORIZATION).description("??????"),
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{??????ID}")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ????????????")
    void ??????_????????????() throws Exception{
        //given
        given(memberService.getMember(1L)).willReturn(initMemberDto.getMemberInfoDto());

        mockMvc.perform(get(urlTemplate + "/{memberId}", 1L)
                        .header(AUTHORIZATION, getJwt(USER,1L)))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("??????")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").type(NUMBER).description("??????ID"),
                                        fieldWithPath("email").type(STRING).description("?????????"),
                                        fieldWithPath("fileName").type(STRING).description("??????????????????").optional(),
                                        fieldWithPath("name").type(STRING).description("??????"),
                                        fieldWithPath("nickname").type(STRING).description("?????????"),
                                        fieldWithPath("gender").type(STRING).description("??????"),
                                        fieldWithPath("birth").type(LOCAL_DATE).description("??????"),
                                        fieldWithPath("tel").type(STRING).description("????????????"),
                                        fieldWithPath("postcode").type(STRING).description("????????????"),
                                        fieldWithPath("address").type(STRING).description("??????"),
                                        fieldWithPath("detailAddress").type(STRING).description("????????????").optional(),
                                        fieldWithPath("extraAddress").type(STRING).description("????????????(??????)"),
                                        fieldWithPath("point").type(NUMBER).description("?????????"),
                                        fieldWithPath("memberSnsDtoList[].snsId").type(NUMBER).description("SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].memberSnsId").type(NUMBER).description("??????SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].snsName").type(STRING).description("SNS ??????").optional(),
                                        fieldWithPath("memberSnsDtoList[].url").type(STRING).description("??????SNS ??????").optional()
                                )
                        )
                );
    }

    @Test
    void ??????_????????????() throws Exception{
        given(memberService.getMemberList(initMemberDto.getSearchRequirements(),initMemberDto.getPageable()))
                .willReturn(initMemberDto.getMemberInfoDtoPage());

        mockMvc.perform(get(urlTemplate)
                        .header(AUTHORIZATION, getJwt(ADMIN, 1L))
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id,desc")
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("??????")
                                ),
                                queryParameters(
                                        parameterWithName("page").attributes(type(NUMBER)).description("?????????").optional(),
                                        parameterWithName("size").attributes(type(NUMBER)).description("?????????").optional(),
                                        parameterWithName("sort").attributes(type(STRING)).description("????????????,????????????").optional(),
                                        parameterWithName("email").attributes(type(STRING)).description("?????????").optional(),
                                        parameterWithName("nickname").attributes(type(STRING)).description("?????????").optional(),
                                        parameterWithName("name").attributes(type(STRING)).description("?????? ??????").optional()
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("content").type(ARRAY).description("?????????"),
                                        fieldWithPath("content.[].memberId").type(NUMBER).description("??????ID"),
                                        fieldWithPath("content.[].email").type(STRING).description("?????????"),
                                        fieldWithPath("content.[].fileName").type(STRING).description("??????????????????").optional(),
                                        fieldWithPath("content.[].name").type(STRING).description("??????"),
                                        fieldWithPath("content.[].nickname").type(STRING).description("?????????"),
                                        fieldWithPath("content.[].gender").type(STRING).description("??????"),
                                        fieldWithPath("content.[].birth").type(LOCAL_DATE).description("??????"),
                                        fieldWithPath("content.[].tel").type(STRING).description("???????????????"),
                                        fieldWithPath("content.[].postcode").type(STRING).description("????????????"),
                                        fieldWithPath("content.[].address").type(STRING).description("??????"),
                                        fieldWithPath("content.[].detailAddress").type(STRING).description("????????????").optional(),
                                        fieldWithPath("content.[].extraAddress").type(STRING).description("?????? ????????????"),
                                        fieldWithPath("content.[].point").type(NUMBER).description("?????????"),
                                        fieldWithPath("content.[].MemberSnsDto.snsId").type(NUMBER).description("??????SNS ID").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.memberSnsId").type(NUMBER).description("?????? ID").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.name").type(STRING).description("SNS ??????").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.url").type(STRING).description("SNS??????").optional(),
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
    @DisplayName("?????? ??????")
    void ??????_??????() throws Exception{
        UpdateMemberDto dto = initMemberDto.getUpdateMemberDto();
        willDoNothing().given(memberService).updateMemberInfo(dto,1L);

        mockMvc.perform(
                createMultiPartRequest(multipart(PUT, urlTemplate + "/{memberId}",1L),dto)
                        .contentType(MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, getJwt(USER,1L))
                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).attributes(info(MULTIPART_FORM_DATA)).description(CONTENT_TYPE),
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("??????")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID")
                                ),
                                requestParts(
                                        partWithName("uploadFile").attributes(type(MULTIPART_FILE)).description("????????? ?????????").optional(),
                                        partWithName("name").attributes(type(STRING)).description("??????"),
                                        partWithName("nickname").attributes(type(STRING)).description("?????????"),
                                        partWithName("gender").attributes(type(STRING)).description("??????"),
                                        partWithName("birth").attributes(type(STRING)).description("??????"),
                                        partWithName("tel").attributes(type(STRING)).description("???????????????"),
                                        partWithName("postcode").attributes(type(STRING)).description("????????????"),
                                        partWithName("address").attributes(type(STRING)).description("??????"),
                                        partWithName("detailAddress").attributes(type(STRING)).description("????????????").optional(),
                                        partWithName("extraAddress").attributes(type(STRING)).description("?????? ????????????"),
                                        partWithName("memberSnsId").attributes(type(arrayType(NUMBER))).description("??????SNS ID").optional(),
                                        partWithName("snsId").attributes(type(arrayType(NUMBER))).description("SNS ID").optional(),
                                        partWithName("url").attributes(type(arrayType(STRING))).description("SNS URL").optional()
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{??????ID}")
                                )
                        )
                );
    }

    @Test
    @DisplayName("???????????? ??????")
    void ??????_????????????_??????()throws Exception{

        willDoNothing().given(memberService).updatePassword(initMemberDto.getUpdatePsswodDto(),1L);

        String json = objectMapper.writeValueAsString(initMemberDto.getUpdatePsswodDto());

        mockMvc.perform(put(urlTemplate + "/{memberId}/password",1L)
                        .header(AUTHORIZATION, getJwt(USER,1L))
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("??????")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID")
                                ),
                                requestFields(
                                        fieldWithPath("newPassword").type(STRING).description("??? ????????????"),
                                        fieldWithPath("confirmPassword").type(STRING).description("??? ???????????? ??????")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{??????ID}")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    void ??????_?????????_??????() throws Exception{
        willDoNothing().given(memberService).updatePoint(1L,initMemberDto.getUpdatePointDto().getPoint());
        String json = objectMapper.writeValueAsString(initMemberDto.getUpdatePointDto());

        mockMvc.perform(put(urlTemplate + "/{memberId}/point",1L)
                        .header(AUTHORIZATION, getJwt(ADMIN,1L))
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
										headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE),
                                        headerWithName(AUTHORIZATION).attributes(info(ADMIN)).description("??????")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID")
                                ),
                                requestFields(
                                        fieldWithPath("point").type(NUMBER).description("?????????")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate)
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ??????")
    void ??????_??????()throws Exception{
        willDoNothing().given(memberService).withdraw(1L);

        mockMvc.perform(delete(urlTemplate + "/{memberId}",1L)
                        .header(AUTHORIZATION, getJwt(USER,1L))
                )
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("??????")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("??????ID")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ??????????????????")
    void ??????_??????????????????()throws Exception{

        String email = "test1@nave.com";
        given(memberService.sendConfirmationEmail(email)).willReturn(initMemberDto.getConfirmationEmailDto());

        mockMvc.perform(get(urlTemplate + "/confirmation-email")
                        .param("email",email)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("email").attributes(type(STRING)).description("?????? ?????????")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").type(NUMBER).description("??????ID"),
                                        fieldWithPath("code").type(STRING).description("????????????")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).description(urlTemplate + "/{??????ID}/password")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ????????? ????????????")
    void ??????_?????????_????????????() throws Exception{
        //given
        String nickname = "??????????????????1";
        willDoNothing().given(memberService).existsNickname(nickname);

        mockMvc.perform(get(urlTemplate + "/nickname")
                    .param("nickname",nickname)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("nickname").attributes(type(STRING)).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ????????????")
    void ??????_?????????_????????????() throws Exception{
        //given
        String email = "test1@naver.com";
        willDoNothing().given(memberService).existsEmail(email);

        mockMvc.perform(get(urlTemplate + "/email")
                        .param("email",email)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("email").attributes(type(STRING)).description("?????? ?????????")
                        )
                ));
    }

}
