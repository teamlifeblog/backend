package com.bethefirst.lifeweb.dto.member.request;

import com.bethefirst.lifeweb.entity.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JoinDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;//이메일

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
//            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
	private String pwd;//비밀번호

	@NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
	private String confirmPwd;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
//    @Pattern(regexp = "/^[\\w\\Wㄱ-ㅎㅏ-ㅣ가-힣]{2,15}$/",
//            message = "닉네임은 특수문자를 제외한 2자 ~ 15자여야 합니다. ")
    private String nickname;//닉네임

	@NotBlank(message = "이름은 필수 입력 값입니다.")
	private String name; //이름

	@NotBlank(message = "성별은 필수 입력 값입니다.")
	private String gender; //성별

	@NotNull(message = "생일은 필수 입력 값입니다.")
	private LocalDate birth; //생일

	@NotBlank(message = "전화번호은 필수 입력 값입니다.")
	private String tel; //전화번호

	@NotBlank(message = "우편번호는 필수 입력 값입니다.")
	private String postcode; //우편번호

	@NotBlank(message = "주소는 필수 입력 값입니다.")
	private String address; //주소

	private String detailAddress; //상세주소

	@NotBlank(message = "참고사항은 필수 입력 값입니다.")
	private String extraAddress; //참고사항

	public Member createMember(PasswordEncoder passwordEncoder) {
		return new Member(email, passwordEncoder.encode(pwd), name, nickname, gender, birth, tel,
				postcode, address, detailAddress, extraAddress);
	}

}
