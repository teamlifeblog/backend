package com.bethefirst.lifeweb.dto.member.request;

import com.bethefirst.lifeweb.entity.member.Sns;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSnsDto {
    private String name; //SNS 이름


    public Sns createSns(){
        return new Sns(name);
    }

}

