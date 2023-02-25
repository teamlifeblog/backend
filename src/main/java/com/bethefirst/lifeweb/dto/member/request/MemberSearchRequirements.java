package com.bethefirst.lifeweb.dto.member.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberSearchRequirements { //회원 검색조건

    private String email;
    private String nickname;
    private String name;

}
