package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.ControllerTest;
import com.bethefirst.lifeweb.config.security.TokenProvider;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.mamber.InitMemberDto;
import com.bethefirst.lifeweb.service.member.interfaces.MemberService;
import com.bethefirst.lifeweb.service.member.interfaces.MemberSnsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.bethefirst.lifeweb.CustomJsonFieldType.LOCAL_DATE_TIME;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(value = MemberController.class)
public class MemberControllerTest extends ControllerTest {


    private String urlTemplate = "/members";
    InitMemberDto initMemberDto = new InitMemberDto();

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    MemberSnsService memberSnsService;

    @MockBean MemberService memberService;


    @Test
    @DisplayName("회원 닉네임 중복체크")
    void 회원_닉네임_중복체크() throws Exception{
        //given
        String nickname = "테스트닉네임1";

        willDoNothing().given(memberService).existsNickname(nickname);

        mockMvc.perform(get(urlTemplate + "/nickname")
                    .param("nickname",nickname)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("nickname").attributes(type(STRING)).description("회원 닉네임")
                        )
                ));
    }


    @Test
    @DisplayName("회원 이메일 중복체크")
    void 회원_이메일_중복체크() throws Exception{
        //given
        String email = "test1@naver.com";
        willDoNothing().given(memberService).existsEmail(email);

        mockMvc.perform(get(urlTemplate + "/email")
                        .param("email",email)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("email").attributes(type(STRING)).description("회원 이메일")
                        )
                ));
    }


    @Test
    @DisplayName("회원 단건조회")
    void 회원_단건조회() throws Exception{
        //given
        given(memberService.getMember(1L)).willReturn(initMemberDto.getMemberInfoDto());


        mockMvc.perform(get(urlTemplate + "/{memberId}", 1L)
                        .header(AUTHORIZATION, getJwt(Role.USER.name(),1L)))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                  headerWithName(AUTHORIZATION).attributes(
                                          role(Role.ADMIN),
                                          role(Role.USER)).description("token")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("회원ID")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").type(NUMBER).description("회원ID"),
                                        fieldWithPath("email").type(STRING).description("이메일"),
                                        fieldWithPath("fileName").type(STRING).description("프로필이미지").optional(),
                                        fieldWithPath("name").type(STRING).description("이름"),
                                        fieldWithPath("nickname").type(STRING).description("닉네임"),
                                        fieldWithPath("gender").type(STRING).description("성별"),
                                        fieldWithPath("birth").type(LOCAL_DATE_TIME).description("생일"),
                                        fieldWithPath("tel").type(STRING).description("전화번호"),
                                        fieldWithPath("postcode").type(STRING).description("우편번호"),
                                        fieldWithPath("address").type(STRING).description("주소"),
                                        fieldWithPath("detailAddress").type(STRING).description("상세주소").optional(),
                                        fieldWithPath("extraAddress").type(STRING).description("참고사항(주소)"),
                                        fieldWithPath("point").type(NUMBER).description("포인트"),
                                        fieldWithPath("memberSnsDtoList[].snsId").type(NUMBER).description("SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].memberSnsId").type(NUMBER).description("회원SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].memberId").type(NUMBER).description("회원 ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].name").type(STRING).description("SNS 이름").optional(),
                                        fieldWithPath("memberSnsDtoList[].url").type(STRING).description("회원SNS 주소").optional()

                                )
                        )
                );

    }

}
