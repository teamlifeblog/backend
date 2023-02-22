package com.bethefirst.lifeweb.dto.member.request;


import com.bethefirst.lifeweb.entity.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UpdateMemberDto {

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

    public void updateMember(Member member){
        member.updateMember(name, nickname, gender, birth, tel, postcode, address, detailAddress, extraAddress);
    }

}
