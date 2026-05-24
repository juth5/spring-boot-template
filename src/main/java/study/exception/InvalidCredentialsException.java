package study.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super("ユーザー名またはパスワードが正しくありません", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}
