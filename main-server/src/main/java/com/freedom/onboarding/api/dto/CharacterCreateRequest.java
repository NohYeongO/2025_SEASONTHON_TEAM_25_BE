package com.freedom.onboarding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 캐릭터 생성 요청 */
@Getter
@NoArgsConstructor
public class CharacterCreateRequest {

    @NotBlank(message = "캐릭터 이름을 입력해주세요.")
    @Size(max = 20, message = "캐릭터 이름은 최대 20자까지 가능합니다.")
    private String characterName;
}
