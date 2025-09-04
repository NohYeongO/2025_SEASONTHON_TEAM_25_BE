package com.freedom.common.exception.custom;

public class CharacterAlreadyCreatedException extends RuntimeException {
    public CharacterAlreadyCreatedException(String message) {
        super("이미 캐릭터가 생성되었습니다.");
    }
}
