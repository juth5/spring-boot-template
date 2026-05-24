package study.config;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import study.exception.BusinessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private Map<String, Object> errorBody(String errorCode, String message, String errorId) {
        return Map.of("error", errorCode, "message", message, "errorId", errorId);
    }

    // -----------------------------------------------------------------------
    // ビジネス例外（UserNotFoundException, InvalidCredentialsException など）
    // -----------------------------------------------------------------------
    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException e, HttpServletRequest request, Model model) {
        log.warn("BusinessException: errorCode={}, URI={}, message={}", e.getErrorCode(), request.getRequestURI(), e.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of("error", e.getErrorCode(), "message", e.getMessage()));
        }
        model.addAttribute("status", e.getStatus().value());
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    // -----------------------------------------------------------------------
    // バリデーションエラー（@Valid が失敗したとき）
    // -----------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException e, HttpServletRequest request, Model model) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("ValidationError: URI={}, details={}", request.getRequestURI(), details);

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "VALIDATION_FAILED", "details", details));
        }
        model.addAttribute("status", 400);
        model.addAttribute("message", "入力値が正しくありません: " + details);
        return "error";
    }

    // -----------------------------------------------------------------------
    // JSONパースエラー（リクエストボディが壊れているとき）
    // -----------------------------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleBadJson(HttpMessageNotReadableException e, HttpServletRequest request, Model model) {
        log.warn("HttpMessageNotReadable: URI={}", request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "INVALID_JSON", "message", "リクエストのJSON形式が正しくありません"));
        }
        model.addAttribute("status", 400);
        model.addAttribute("message", "リクエストの形式が正しくありません");
        return "error";
    }

    // -----------------------------------------------------------------------
    // パス変数の型ミスマッチ（例: /api/calllogs/abc）
    // -----------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorID={} TypeMismatch: URI={}, param={}", errorId, request.getRequestURI(), e.getName());

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(errorBody("BAD_REQUEST", "パラメータの型が正しくありません: " + e.getName(), errorId));
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 400);
        model.addAttribute("message", "不正なリクエストです");
        return "error";
    }

    // -----------------------------------------------------------------------
    // 404（存在しないパス）
    // -----------------------------------------------------------------------
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNotFound(NoHandlerFoundException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.info("ErrorID={} 404 Not Found: URI={}", errorId, request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorBody("NOT_FOUND", "APIリソースが見つかりません", errorId));
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 404);
        model.addAttribute("message", "ページが見つかりません");
        return "error";
    }

    // -----------------------------------------------------------------------
    // 403（権限不足。Spring Security が投げる AccessDeniedException）
    // -----------------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException e, HttpServletRequest request, Model model) {
        log.warn("AccessDenied: URI={}", request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "FORBIDDEN", "message", "このリソースへのアクセス権限がありません"));
        }
        model.addAttribute("status", 403);
        model.addAttribute("message", "アクセス権限がありません");
        return "error";
    }

    // -----------------------------------------------------------------------
    // DB 一意制約違反（サービス層で捕まえられなかった場合の安全網）
    // -----------------------------------------------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleConstraintError(DataIntegrityViolationException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorID={} ConstraintViolation: URI={}", errorId, request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(errorBody("DUPLICATE_ENTRY", "一意制約に違反しています", errorId));
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 409);
        model.addAttribute("message", "入力値が一意制約に違反しています");
        return "error";
    }

    // -----------------------------------------------------------------------
    // DB アクセスエラー全般
    // -----------------------------------------------------------------------
    @ExceptionHandler(DataAccessException.class)
    public Object handleDatabaseError(DataAccessException e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorID={} DatabaseError: URI={}", errorId, request.getRequestURI(), e);

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("DATABASE_ERROR", "データベースエラーが発生しました", errorId));
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 500);
        model.addAttribute("message", "データベースエラーが発生しました");
        return "error";
    }

    // -----------------------------------------------------------------------
    // 予期しない例外（最後の砦）
    // -----------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public Object handleUnexpected(Exception e, HttpServletRequest request, Model model) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorID={} UnexpectedError: URI={}", errorId, request.getRequestURI(), e);

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("INTERNAL_SERVER_ERROR", "サーバー内部でエラーが発生しました", errorId));
        }
        model.addAttribute("errorId", errorId);
        model.addAttribute("status", 500);
        model.addAttribute("message", "サーバー内部でエラーが発生しました");
        return "error";
    }
}
