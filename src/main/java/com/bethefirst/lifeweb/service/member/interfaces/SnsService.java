package com.bethefirst.lifeweb.service.member.interfaces;

import com.bethefirst.lifeweb.dto.member.response.SnsDto;
import com.bethefirst.lifeweb.dto.member.request.CreateSnsDto;
import com.bethefirst.lifeweb.dto.member.request.UpdateSnsDto;

import java.util.List;

public interface SnsService {

    /** SNS 등록 */
    Long createSns(CreateSnsDto createSnsDto);

    /** SNS 단건조회 */
    SnsDto getSns(Long snsId);

    /** SNS 전체조회 */
    List<SnsDto> getSnsList();

    /** SNS 수정 */
    void updateSns(UpdateSnsDto updateSnsDto, Long snsId);

    /** SNS 삭제 */
    void deleteSns(Long snsId);


}
