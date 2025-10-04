package study.config;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.servlet.http.HttpServletRequest;



@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400系: 不正リクエスト
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleBadRequest(MethodArgumentTypeMismatchException e, Model model, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorID={} 400 Bad Request: URI={}, Message={}", errorId, request.getRequestURI(), e.getMessage(), e);

        model.addAttribute("errorId", errorId);
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", "不正なリクエストです");
        return "error";
    }

    // 404系: ページが存在しない (API/ページを1つのメソッドで振り分け)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNotFound(NoHandlerFoundException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.info("ErrorID={} 404 Not Found: URI={}", errorId, request.getRequestURI());

        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Not Found");
            body.put("message", "APIリソースが見つかりません");
            body.put("errorId", errorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", "ページが見つかりません");
        return "error";
    }

    // 500系: サーバーエラー（最後の砦）
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorID={} 500 Internal Server Error: URI={}, Message={}", errorId, request.getRequestURI(), e.getMessage(), e);

        model.addAttribute("errorId", errorId);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "サーバー内部でエラーが発生しました");
        return "error";
    }

    // SpringがラップしたDBエラー（共通親クラス）
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseError(DataAccessException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorID={} Database Error: URI={}, Message={}", errorId, request.getRequestURI(), e.getMessage(), e);

        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 500);
        model.addAttribute("message", "データベースエラーが発生しました");
        return "error";
    }

    // 特定の制約違反（ユニーク制約など）
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleConstraintError(DataIntegrityViolationException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorID={} Constraint Violation: URI={}, Message={}", errorId, request.getRequestURI(), e.getMessage(), e);

        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 400);
        model.addAttribute("message", "入力値が一意制約に違反しています");
        return "error";
    }
}