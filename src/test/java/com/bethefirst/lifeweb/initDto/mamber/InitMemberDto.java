package com.bethefirst.lifeweb.initDto.mamber;

import com.bethefirst.lifeweb.dto.member.request.*;
import com.bethefirst.lifeweb.dto.member.response.MemberInfoDto;
import com.bethefirst.lifeweb.dto.member.response.MemberSnsDto;
import com.bethefirst.lifeweb.initDto.InitMockMultipartFile;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDate.now;

public class InitMemberDto {

    InitMemberSnsDto initMemberSnsDto = new InitMemberSnsDto();
    InitMockMultipartFile mockMultipartFile = new InitMockMultipartFile();

    public LoginDto getLoginDto() {
        return new LoginDto("test1@naver.com", "a1231231#");
    }

    public JoinDto getJoinDto() {
        return new JoinDto("test1@naver.com", "a1231231#", "닉네임", "이름", "남자", now(), "01000000001", "11901", "경기 구리시 갈매동 215-56", "1층", "(갈매동))");
    }

    public MemberInfoDto getMemberInfoDto() {

        return getMemberInfoDtoList().get(0);
    }

    public MemberSearchRequirements getSearchRequirements() {
        return MemberSearchRequirements.builder().build();

    }

    public Pageable getPageable(){
        return pageable;
    }
    public Page<MemberInfoDto> getMemberInfoDtoPage() {
        return new PageImpl(getSubList(), pageable, getMemberInfoDtoList().size());
    }

    private List<MemberInfoDto> getSubList() {

        if (getMemberInfoDtoList().size() < getPage() * getSize()) {
            return new ArrayList<>();
        } else if (getMemberInfoDtoList().size() - getPage() * getSize() <= getSize()) {
            return getMemberInfoDtoList().subList(getPage() * getSize(), getMemberInfoDtoList().size());
        } else {
            return getMemberInfoDtoList().subList(getPage() * getSize(), getSize());
        }
    }

    public List<MemberInfoDto> getMemberInfoDtoList() {

        List<MemberSnsDto> memberSnsDtoList = initMemberSnsDto.getMemberSnsDtoList();
        List<MemberInfoDto> memberInfoDtoList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            String gender = "남자";
            if (i % 2 == 0) {
                gender = "여자";
            }

            MemberInfoDto memberInfoDto = new MemberInfoDto((long) i + 1, "test" + i + "@naver.com", UUID.randomUUID().toString() + ".jpg", "테스트이름" + i,
                    "테스트닉네임" + i, gender, now(), "0100000000" + i, "1190" + i, "경기 구리시 갈매동 215-56" + i, i + "층", "(갈매동)",
                    0, new ArrayList<>());
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


    public UpdateMemberDto getUpdateMemberDto(){

        return new UpdateMemberDto(mockMultipartFile.getMockMultipartFile(), "수정할 이름", "수정 닉네임", "남자", now(), "01000000001", "11901", "경기 구리시 갈매동 215-56",
                "1층", "(갈매동)", Arrays.asList(0L), Arrays.asList(1L), Arrays.asList("blog.naver.com/myblog"));

    }


    public UpdatePasswordDto getUpdatePsswodDto(){
        return new UpdatePasswordDto("a123123a#","a123123a#");
    }

    private Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "id"));

    private int getPage() {
        return pageable.getPageNumber();
    }

    private int getSize() {
        return pageable.getPageSize();
    }
}

