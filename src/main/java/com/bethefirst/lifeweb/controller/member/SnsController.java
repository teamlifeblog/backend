package com.bethefirst.lifeweb.controller.member;

import com.bethefirst.lifeweb.dto.member.request.CreateSnsDto;
import com.bethefirst.lifeweb.dto.member.request.UpdateSnsDto;
import com.bethefirst.lifeweb.dto.member.response.SnsDto;
import com.bethefirst.lifeweb.service.member.interfaces.SnsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_LOCATION;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/sns")
@RequiredArgsConstructor
@Slf4j
public class SnsController {

    private final SnsService snsService;

    /** SNS 등록 */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateSnsDto createSnsDto){
        Long saveSnsId = snsService.createSns(createSnsDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_LOCATION, "/sns/" + saveSnsId);
        return new ResponseEntity<>(headers, CREATED);

    }

    /** SNS 단건조회 */
    @ResponseStatus(OK)
    @GetMapping("/{snsId}")
    public SnsDto detail(@PathVariable Long snsId){
        return snsService.getSns(snsId);
    }

    /** SNS 전체조회 */
    @ResponseStatus(OK)
    @GetMapping
    public List<SnsDto> list(){
        return snsService.getSnsList();
    }


    /** SNS 수정 */
    @PutMapping("/{snsId}")
    public ResponseEntity<?> update(@PathVariable Long snsId,
                       @Valid @RequestBody UpdateSnsDto updateSnsDto){
        snsService.updateSns(updateSnsDto, snsId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_LOCATION, "/sns/" + snsId);
        return new ResponseEntity<>(headers, CREATED);
    }


    /** SNS 삭제 */
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{snsId}")
    public void delete(@PathVariable Long snsId){
        snsService.deleteSns(snsId);

    }



}
