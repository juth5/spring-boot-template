package study.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends BusinessException {

    public DuplicateUsernameException(String username) {
        super("ユーザー名はすでに使用されています: " + username, HttpStatus.CONFLICT, "DUPLICATE_USERNAME");
    }
}
