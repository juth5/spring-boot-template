package study.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String username) {
        super("ユーザーが見つかりません: " + username, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}
