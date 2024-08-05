package usw.suwiki.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {
    private final T data;
    private final boolean success;

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(data, true);
    }

    public static CommonResponse<?> success() { return new CommonResponse<>(null,true); }
}

