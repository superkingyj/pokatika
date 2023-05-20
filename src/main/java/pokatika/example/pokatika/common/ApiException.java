package pokatika.example.pokatika.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException{
    private final StatusMessage statusMessage;
}
