package pokatika.example.pokatika.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<T> {
    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final T data;

    private ApiResponse(StatusMessage statusMessage, T data){
        this.httpStatus = statusMessage.getHttpStatus();
        this.message = statusMessage.getMessage();
        this.data = data;
    }

    public static ResponseEntity<ApiResponse<?>> exception(StatusMessage statusMessage){
        ApiResponse<Object> apiResponse = new ApiResponse<>(statusMessage, null);
        return new ResponseEntity<>(apiResponse, apiResponse.httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successWitchBody(StatusMessage statusMessage, T data){
        ApiResponse<T> apiResponse = new ApiResponse<>(statusMessage, data);
        return new ResponseEntity<>(apiResponse, apiResponse.httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successWithNothing(StatusMessage statusMessage){
        ApiResponse<T> apiResponse = new ApiResponse<>(statusMessage, null);
        return new ResponseEntity<>(apiResponse, apiResponse.httpStatus);
    }
}
