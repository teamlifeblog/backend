package com.bethefirst.lifeweb.service.member.interfaces;


import com.bethefirst.lifeweb.dto.member.request.JoinDto;
import com.bethefirst.lifeweb.dto.member.request.MemberUpdateDto;
import com.bethefirst.lifeweb.dto.member.request.PasswordDto;
import com.bethefirst.lifeweb.dto.member.response.MemberInfoDto;
import org.springframework.web.multipart.MultipartFile;


public interface MemberService {

    /** 회원 가입 */
    void join(JoinDto joinDto);

    /** 회원정보 수정 */
    void updateMemberInfo(MemberUpdateDto memberUpdateDto, Long memberId);

    /** 회원 이미지 수정 */
    void updateMemberImage(MultipartFile memberFileName, Long memberId);

    /** 회원 비밀번호 변경 */
    void updatePassword(PasswordDto passwordDto, Long memberId);

    /** 회원 정보조회 */
    MemberInfoDto getInfo(Long memberId);

    /** 회원탈퇴 **/

}
