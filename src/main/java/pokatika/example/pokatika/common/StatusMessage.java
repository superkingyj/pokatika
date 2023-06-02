package pokatika.example.pokatika.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum StatusMessage {

    // 200 ok
    SUCCESS(OK, "요청을 성공적으로 처리했습니다"),

    // 404 NOT_FOUND
    PARTICIPANT_NOT_FOUND(NOT_FOUND, "이벤트 참여자를 찾을 수 없습니다."),
    EVENT_NOT_FOUND(NOT_FOUND, "이벤트를 찾을 수 없습니다."),
    NFT_NOT_FOUND(NOT_FOUND, "NFT를 찾을 수 없습니다"),
    IMAGE_NOT_FOUND(NOT_FOUND, "이미지를 찾을 수 없습니다."),

    // 500 INTERNAL_SEVER_ERROR
    IMAGE_REMAKE_ERROR(INTERNAL_SERVER_ERROR, "NFT 이미지 제작 중 문제가 발생했습니다."),
    IMAGE_SAVE_ERROR(INTERNAL_SERVER_ERROR, "NFT 이미지 저장 중 문제가 발생했습니다."),
    ERROR(INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
