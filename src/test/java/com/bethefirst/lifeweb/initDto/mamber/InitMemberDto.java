package com.bethefirst.lifeweb.initDto.mamber;

import com.bethefirst.lifeweb.dto.member.request.LoginDto;
import com.bethefirst.lifeweb.dto.member.response.MemberInfoDto;
import com.bethefirst.lifeweb.dto.member.response.MemberSnsDto;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;

public class InitMemberDto {

    InitMemberSnsDto initMemberSnsDto = new InitMemberSnsDto();

    public LoginDto getLoginDto(){
        return new LoginDto("test1@naver.com", "a1231231#");
    }


    public MemberInfoDto getMemberInfoDto(){

        return getMemberInfoDtoList().get(0);
    }

    public List<MemberInfoDto> getMemberInfoDtoList() {

        List<MemberSnsDto> memberSnsDtoList = initMemberSnsDto.getMemberSnsDtoList();
        List<MemberInfoDto> memberInfoDtoList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            String gender = "남자";
            if (i % 2 == 0) {
                gender = "여자";
            }

            MemberInfoDto memberInfoDto = new MemberInfoDto((long) i + 1, "test" + i + "@naver.com", null, "테스트이름" + i,
                    "테스트닉네임" + i, gender, now(), "0100000000" + i, "1190" + i, "경기 구리시 갈매동 215-56" + i, i + "층", "(갈매동)",
                    0,new ArrayList<>());
            memberInfoDtoList.add(memberInfoDto);

        }


        for (int j = 0; j < memberInfoDtoList.size(); j++) {

            MemberInfoDto findInfoDto = memberInfoDtoList.get(j);
            for (int k = 0; k < 3; k++) {
                MemberSnsDto memberSnsDto = memberSnsDtoList.get(j * 3 + k);
                findInfoDto.addMemberSnsDto(memberSnsDto);
            }
        }

        return memberInfoDtoList;
    }
}

