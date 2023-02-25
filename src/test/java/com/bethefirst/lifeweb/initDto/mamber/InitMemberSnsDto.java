package com.bethefirst.lifeweb.initDto.mamber;

import com.bethefirst.lifeweb.dto.member.response.MemberSnsDto;
import com.bethefirst.lifeweb.dto.member.response.SnsDto;

import java.util.ArrayList;
import java.util.List;

public class InitMemberSnsDto {

     InitSnsDto initSnsDto = new InitSnsDto();


     public MemberSnsDto getMemberSnsDto(){
         return getMemberSnsDtoList().get(0);
     }


     public List<MemberSnsDto> getMemberSnsDtoList(){

         List<SnsDto> snsDtoList = initSnsDto.getSnsDtoList();
         List<MemberSnsDto> memberSnsDtoList = new ArrayList<>();

         for(int i=1; i < 11; i++ ){

             for(int j=0; j < snsDtoList.size(); j++){
                 MemberSnsDto memberSnsDto = new MemberSnsDto(((long) (i - 1) * snsDtoList.size() + j + 1),
                         snsDtoList.get(j).getId(),
                         snsDtoList.get(j).getName(),
                         "www." + snsDtoList.get(j).getName()+ ".com/random" + j
                         );
                 memberSnsDtoList.add(memberSnsDto);
             }

         }
         return memberSnsDtoList;
     }

}
