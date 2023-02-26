package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.config.security.TokenProvider;
import com.bethefirst.lifeweb.controller.ControllerTest;
import com.bethefirst.lifeweb.dto.member.request.UpdateMemberDto;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.initDto.mamber.InitMemberDto;
import com.bethefirst.lifeweb.service.member.interfaces.MemberService;
import com.bethefirst.lifeweb.service.member.interfaces.MemberSnsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.bethefirst.lifeweb.entity.member.Role.ADMIN;
import static com.bethefirst.lifeweb.entity.member.Role.USER;
import static com.bethefirst.lifeweb.util.CustomJsonFieldType.LOCAL_DATE;
import static com.bethefirst.lifeweb.util.CustomJsonFieldType.MULTIPART_FILE;
import static com.bethefirst.lifeweb.util.CustomRestDocumentationRequestBuilders.multipart;
import static com.bethefirst.lifeweb.util.RestdocsUtil.createMultiPartRequest;
import static com.bethefirst.lifeweb.util.RestdocsUtil.getJwt;
import static com.bethefirst.lifeweb.util.SnippetUtil.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
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
                        .header(AUTHORIZATION, getJwt(USER,1L)))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                  headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("token")
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
                                        fieldWithPath("birth").type(LOCAL_DATE).description("생일"),
                                        fieldWithPath("tel").type(STRING).description("전화번호"),
                                        fieldWithPath("postcode").type(STRING).description("우편번호"),
                                        fieldWithPath("address").type(STRING).description("주소"),
                                        fieldWithPath("detailAddress").type(STRING).description("상세주소").optional(),
                                        fieldWithPath("extraAddress").type(STRING).description("참고사항(주소)"),
                                        fieldWithPath("point").type(NUMBER).description("포인트"),
                                        fieldWithPath("memberSnsDtoList[].snsId").type(NUMBER).description("SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].memberSnsId").type(NUMBER).description("회원SNS ID").optional(),
                                        fieldWithPath("memberSnsDtoList[].snsName").type(STRING).description("SNS 이름").optional(),
                                        fieldWithPath("memberSnsDtoList[].url").type(STRING).description("회원SNS 주소").optional()

                                )
                        )
                );

    }

    @Test
    @DisplayName("회원 가입")
    void 회원_가입() throws Exception{

        willDoNothing().given(memberService).join(initMemberDto.getJoinDto());

        String json = objectMapper.writeValueAsString(initMemberDto.getJoinDto());

        mockMvc.perform(post(urlTemplate)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        .characterEncoding(UTF_8))
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                  headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(CONTENT_TYPE)
                                ),
                                requestFields(
                                        fieldWithPath("email").attributes(type(STRING)).description("이메일"),
                                        fieldWithPath("pwd").attributes(type(STRING)).description("비밀번호"),
                                        fieldWithPath("nickname").attributes(type(STRING)).description("닉네임"),
                                        fieldWithPath("name").attributes(type(STRING)).description("이름"),
                                        fieldWithPath("gender").attributes(type(STRING)).description("성별"),
                                        fieldWithPath("birth").attributes(type(LOCAL_DATE)).description("생일"),
                                        fieldWithPath("tel").attributes(type(STRING)).description("휴대폰번호"),
                                        fieldWithPath("postcode").attributes(type(STRING)).description("우편번호"),
                                        fieldWithPath("address").attributes(type(STRING)).description("주소"),
                                        fieldWithPath("detailAddress").attributes(type(STRING)).description("상세주소").optional(),
                                        fieldWithPath("extraAddress").attributes(type(STRING)).description("참고사항")

                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate + "/login")).description(CONTENT_LOCATION)
                                )
                        )
                );



    }

    @Test
    void 회원_전체조회() throws Exception{
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
                                        headerWithName(AUTHORIZATION).attributes(info(Role.ADMIN)).description("token")

                                ),
                                queryParameters(
                                        parameterWithName("page").attributes(type(NUMBER)).description("페이지").optional(),
                                        parameterWithName("size").attributes(type(NUMBER)).description("사이즈").optional(),
                                        parameterWithName("sort").attributes(type(STRING)).description("정렬기준,정렬방향").optional(),
                                        parameterWithName("email").attributes(type(STRING)).description("이메일").optional(),
                                        parameterWithName("nickname").attributes(type(STRING)).description("닉네임").optional(),
                                        parameterWithName("name").attributes(type(STRING)).description("회원 이름").optional()
                                ),
                                relaxedResponseFields(
                                        fieldWithPath("content").type(ARRAY).description("리스트"),
                                        fieldWithPath("content.[].memberId").type(NUMBER).description("회원ID"),
                                        fieldWithPath("content.[].email").type(STRING).description("이메일"),
                                        fieldWithPath("content.[].fileName").type(STRING).description("프로필이미지").optional(),
                                        fieldWithPath("content.[].name").type(STRING).description("이름"),
                                        fieldWithPath("content.[].nickname").type(STRING).description("닉네임"),
                                        fieldWithPath("content.[].gender").type(STRING).description("남자"),
                                        fieldWithPath("content.[].birth").type(LOCAL_DATE).description("생일"),
                                        fieldWithPath("content.[].tel").type(STRING).description("휴대폰번호"),
                                        fieldWithPath("content.[].postcode").type(STRING).description("우편번호"),
                                        fieldWithPath("content.[].address").type(STRING).description("주소"),
                                        fieldWithPath("content.[].detailAddress").type(STRING).description("상세주소").optional(),
                                        fieldWithPath("content.[].extraAddress").type(STRING).description("주소 참고사항"),
                                        fieldWithPath("content.[].point").type(NUMBER).description("포인트"),
                                        fieldWithPath("content.[].MemberSnsDto.snsId").type(NUMBER).description("회원SNS ID").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.memberSnsId").type(NUMBER).description("회원 ID").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.name").type(STRING).description("SNS 이름").optional(),
                                        fieldWithPath("content.[].MemberSnsDto.url").type(STRING).description("SNS주소").optional(),
                                        fieldWithPath("pageable").type(Pageable.class.getSimpleName()).description("페이징"),
                                        fieldWithPath("last").type(BOOLEAN).description("마지막페이지"),
                                        fieldWithPath("totalPages").type(NUMBER).description("전체페이지"),
                                        fieldWithPath("totalElements").type(NUMBER).description("전체엘리먼트"),
                                        fieldWithPath("size").type(NUMBER).description("사이즈"),
                                        fieldWithPath("number").type(NUMBER).description("페이지"),
                                        fieldWithPath("sort").type(Sort.class.getSimpleName()).description("정렬"),
                                        fieldWithPath("first").type(BOOLEAN).description("첫페이지"),
                                        fieldWithPath("numberOfElements").type(NUMBER).description("엘리먼트"),
                                        fieldWithPath("empty").type(BOOLEAN).description("empty")
                                )
                        )

                );
    }

    @Test
    @DisplayName("회원 수정")
    void 회원_수정() throws Exception{
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
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description(AUTHORIZATION)
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("회원ID")
                                ),
                                requestParts(
                                        partWithName("uploadFile").attributes(type(MULTIPART_FILE)).description("프로필 이미지").optional(),
                                        partWithName("name").attributes(type(STRING)).description("이름"),
                                        partWithName("nickname").attributes(type(STRING)).description("닉네임"),
                                        partWithName("gender").attributes(type(STRING)).description("성별"),
                                        partWithName("birth").attributes(type(STRING)).description("생일"),
                                        partWithName("tel").attributes(type(STRING)).description("휴대폰번호"),
                                        partWithName("postcode").attributes(type(STRING)).description("우편번호"),
                                        partWithName("address").attributes(type(STRING)).description("주소"),
                                        partWithName("detailAddress").attributes(type(STRING)).description("상세주소").optional(),
                                        partWithName("extraAddress").attributes(type(STRING)).description("주소 참고사항"),
                                        partWithName("memberSnsId").attributes(type(arrayType(NUMBER))).description("회원SNS ID").optional(),
                                        partWithName("snsId").attributes(type(arrayType(NUMBER))).description("SNS ID").optional(),
                                        partWithName("url").attributes(type(arrayType(STRING))).description("SNS URL").optional()

                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate + "/{memberId}")).description(CONTENT_LOCATION)
                                )

                        )
                );
    }

    @Test
    @DisplayName("회원 탈퇴")
    void 회원_탈퇴()throws Exception{
        willDoNothing().given(memberService).withdraw(1L);

        mockMvc.perform(delete(urlTemplate + "/{memberId}",1L)
                        .header(AUTHORIZATION,getJwt(USER,1L))
                )
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("token")
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("회원ID")
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 변경")
    void 회원_비밀번호_변경()throws Exception{

        willDoNothing().given(memberService).updatePassword(initMemberDto.getUpdatePsswodDto(),1L);

        String json = objectMapper.writeValueAsString(initMemberDto.getUpdatePsswodDto());

        mockMvc.perform(put(urlTemplate + "/{memberId}/password",1L)
                        .header(AUTHORIZATION,getJwt(USER,1L))
                        .contentType(APPLICATION_JSON)
                        .content(json)

                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).attributes(info(USER,ADMIN)).description("token"),
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(APPLICATION_JSON)
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("회원ID")
                                ),
                                requestFields(
                                        fieldWithPath("newPassword").attributes(type(STRING)).description("새 비밀번호"),
                                        fieldWithPath("confirmPassword").attributes(type(STRING)).description("새 비밀번호 확인")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate + "/{memberId}")).description(CONTENT_LOCATION)
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원 포인트 수정")
    void 회원_포인트_수정() throws Exception{
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
                                        headerWithName(AUTHORIZATION).attributes(info(ADMIN)).description("token"),
                                        headerWithName(CONTENT_TYPE).attributes(info(APPLICATION_JSON)).description(APPLICATION_JSON)
                                ),
                                pathParameters(
                                        parameterWithName("memberId").attributes(type(NUMBER)).description("회원ID")
                                ),
                                requestFields(
                                        fieldWithPath("point").attributes(type(NUMBER)).description("포인트")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate)).description(CONTENT_LOCATION)
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원 인증메일전송")
    void 회원_인증메일전송()throws Exception{

        String email = "test1@nave.com";
        given(memberService.sendConfirmationEmail(email)).willReturn(initMemberDto.getConfirmationEmailDto());

        mockMvc.perform(get(urlTemplate + "/confirmation-email")
                        .param("email",email)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("email").attributes(type(STRING)).description("회원 이메일")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").attributes(type(NUMBER)).description("회원ID"),
                                        fieldWithPath("code").attributes(type(STRING)).description("인증번호")
                                ),
                                responseHeaders(
                                        headerWithName(CONTENT_LOCATION).attributes(path(urlTemplate + "/{memberId}/password")).description(CONTENT_LOCATION)
                                )
                        )
                );

    }


}
