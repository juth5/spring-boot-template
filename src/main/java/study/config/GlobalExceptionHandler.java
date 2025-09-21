package study.config;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400系: 不正リクエスト
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleBadRequest(MethodArgumentTypeMismatchException e, Model model) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", "不正なリクエストです");
        return "error";
    }

    // 404系: ページが存在しない
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException e, Model model) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", "ページが見つかりません");
        return "error";
    }

    // 500系: サーバーエラー
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception e, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "サーバー内部でエラーが発生しました");
        return "error";
    }
}