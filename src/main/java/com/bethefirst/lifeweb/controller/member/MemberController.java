package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.config.security.JwtFilter;
import com.bethefirst.lifeweb.config.security.TokenProvider;
import com.bethefirst.lifeweb.dto.CustomUser;
import com.bethefirst.lifeweb.dto.jwt.TokenDto;
import com.bethefirst.lifeweb.dto.member.request.*;
import com.bethefirst.lifeweb.dto.member.response.ConfirmationEmailDto;
import com.bethefirst.lifeweb.dto.member.response.MemberInfoDto;
import com.bethefirst.lifeweb.service.member.interfaces.MemberService;
import com.bethefirst.lifeweb.service.member.interfaces.MemberSnsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpHeaders.CONTENT_LOCATION;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MemberController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;
    private final MemberSnsService memberSnsService;

    /** 회원 가입 */
    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody JoinDto joinDto) {

        memberService.join(joinDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_LOCATION,"/members/login");

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /** 회원정보 수정 */
    @PutMapping("/{memberId}")
    @PreAuthorize("isAuthenticated() and (( #memberId == principal.memberId ) or hasRole('ADMIN'))")
    public ResponseEntity<?> update(@PathVariable Long memberId,
                             		@Valid @RequestBody UpdateMemberDto updateMemberDto) {

        memberService.updateMemberInfo(updateMemberDto, memberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/members/" + memberId));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPwd());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        CustomUser principal = (CustomUser) authentication.getPrincipal();
        Long memberId = principal.getMemberId();
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        headers.set(CONTENT_LOCATION,"/members/" + memberId);
        return new ResponseEntity<>(new TokenDto(jwt), headers, HttpStatus.OK);

    }

    /** 회원 탈퇴 */
    @DeleteMapping("/{memberId}")
    @PreAuthorize("isAuthenticated() and (( #memberId == principal.memberId ) or hasRole('ADMIN'))")
    public ResponseEntity<?> withdraw(@PathVariable Long memberId){

        memberService.withdraw(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    /** 회원 단건 조회 */
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated() and (( #memberId == principal.memberId ) or hasRole('ADMIN'))")
    public MemberInfoDto read(@PathVariable Long memberId) {

        return memberService.getMember(memberId);
    }

    /** 회원 전체 조회 */
    @GetMapping
    public Page<MemberInfoDto> list(MemberSearchRequirements requirements,
                                    @PageableDefault(sort = "id", size = 20, direction = Sort.Direction.DESC)Pageable pageable){
        return memberService.getMemberList(requirements, pageable);
    }

    /** 비밀번호 변경 */
    @PutMapping("/{memberId}/password")
    @PreAuthorize("!isAuthenticated() or (isAuthenticated() and (( #memberId == principal.memberId ) or hasRole('ADMIN')))")
    public ResponseEntity<?> updatePassword(@PathVariable Long memberId,
                                            @RequestBody UpdatePasswordDto updatePasswordDto){

        memberService.updatePassword(updatePasswordDto, memberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/members/" + memberId));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

	/** 포인트 수정 */
	@PutMapping("/{memberId}/point")
	public ResponseEntity<?> updatePoint(@PathVariable Long memberId,
							@RequestBody UpdatePointDto updatePointDto) {
		memberService.updatePoint(memberId, updatePointDto.getPoint());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/members"));

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

    /** 닉네임 중복 체크 */
    @GetMapping("/nickname")
    public void existNickname(@Valid @NotBlank(message = "닉네임은 필수 값 입니다.") String nickname){
        memberService.existsNickname(nickname);
    }

    /** 이메일 중복 체크 */
    @GetMapping("/email")
    public void existEmail(@Valid @NotBlank(message ="이메일은 필수 값 입니다.") String email){
        memberService.existsEmail(email);
    }

    /** 인증 메일 전송 */
    @GetMapping("/confirmation-email")
    public ResponseEntity<?> confirmationEmail(String email) {

		ConfirmationEmailDto confirmationEmailDto = memberService.sendConfirmationEmail(email);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/members/" + confirmationEmailDto.getMemberId() + "/password"));

		return new ResponseEntity<>(confirmationEmailDto, headers, HttpStatus.OK);
    }

}
