package pokatika.example.pokatika.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handelApiException(ApiException e){
        StatusMessage statusMessage = e.getStatusMessage();
        log.error(String.format("ApiException: %s - %d", statusMessage.getMessage(), statusMessage.getHttpStatus().value()));
        return ApiResponse.exception(e.getStatusMessage());
    }
}
