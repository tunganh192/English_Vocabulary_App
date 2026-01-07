package com.honda.englishapp.english_learning_backend.exception;

import com.honda.englishapp.english_learning_backend.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHanlder {

    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";


    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception){
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGRIED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGRIED_EXCEPTION.getMessage());
        //apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode =exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse apiResponse = new ApiResponse();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }



//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
//        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
//
//        log.warn("DataIntegrityViolationException root cause: {}", message);
//
//        ErrorCode errorCode = ErrorCode.INVALID_KEY;
//
//        if (message != null) {
//            String lowerMsg = message.toLowerCase();
//
//            if (lowerMsg.contains("duplicate") || lowerMsg.contains("unique")) {
//                if (lowerMsg.contains("username")) {
//                    errorCode = ErrorCode.USERNAME_EXISTED;
//                } else if (lowerMsg.contains("category.code")) {
//                    errorCode = ErrorCode.CATEGORY_CODE_EXISTED;
//                } else {
//                    errorCode = ErrorCode.INVALID_KEY;
//                }
//
//            }
////            else if (lowerMsg.contains("foreign key")) {
////                if (lowerMsg.contains("user_id")) {
////                    errorCode = ErrorCode.USER_NOT_EXISTED;
////                } else if (lowerMsg.contains("category_id")) {
////                    errorCode = ErrorCode.CATEGORY_NOT_EXISTED;
////                } else {
////                    errorCode = ErrorCode.INVALID_KEY;
////                }
////            }
//        }
//
//        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
//                ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build()
//        );
//    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        if (message != null) {
            String lowerMsg = message.toLowerCase();
            if (lowerMsg.contains("duplicate") || lowerMsg.contains("unique")) {
                if (lowerMsg.contains("uksb8bbouer5wak8vyiiy4pf2bx")) {
                    errorCode = ErrorCode.USERNAME_EXISTED;
                } else if (lowerMsg.contains("ukacatplu22q5d1andql2jbvjy7")) {
                    errorCode = ErrorCode.CATEGORY_CODE_EXISTED;
                } else {
                    errorCode = ErrorCode.INVALID_KEY;
                }
            }
        }

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse> handlingValid(MethodArgumentNotValidException exception){
//        String enumKey = exception.getFieldError().getDefaultMessage();
//
//        ErrorCode errorCode = ErrorCode.INVALID_KEY;
//        Map<String, Object> attribute = null;
//
//        try {
//            errorCode = ErrorCode.valueOf(enumKey);
//
//            // có object chứa thông tin attribute
//            var constrainViolation = exception.getBindingResult() // là những error mà exception wrap(bọc) lại
//                    .getAllErrors().getFirst().unwrap(ConstraintViolation.class);
//
//            attribute = constrainViolation.getConstraintDescriptor().getAttributes();  // trả về 1 map
//
//        }catch (IllegalArgumentException e){
//
//        }
//
//        ApiResponse apiResponse = new ApiResponse();
//
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(Objects.nonNull(attribute) ?
//                mapAttributes(errorCode.getMessage(), attribute)
//                : errorCode.getMessage());
//
//        return ResponseEntity.badRequest().body(apiResponse);
//    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValid(MethodArgumentNotValidException exception) {
        String enumKey = null;
        Map<String, Object> attributes = null;

        // Lấy ObjectError đầu tiên (hỗ trợ class-level validation)
        ObjectError objectError = exception.getBindingResult().getAllErrors().getFirst(); //trả về danh sách các lỗi validation, bao gồm cả FieldError (lỗi field-level, như @Size) và ObjectError (lỗi class-level, như @NotEmptyPatchRequest).
        if (objectError != null) {
            enumKey = objectError.getDefaultMessage();
        }

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
            // Nếu là FieldError, lấy attributes từ ConstraintViolation
            if (objectError.unwrap(ConstraintViolation.class) != null) {
                ConstraintViolation<?> constraintViolation = objectError.unwrap(ConstraintViolation.class);
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Invalid error code: {}", enumKey, e);
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ?
                mapAttributes(errorCode.getMessage(), attributes) : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // bắt các lỗi valid request
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    ResponseEntity<ApiResponse> handlingConstraintViolation(ConstraintViolationException exception) {
//        String enumKey = exception.getConstraintViolations().iterator().next().getMessage();
//        ErrorCode errorCode = ErrorCode.INVALID_KEY;
//
//        try {
//            errorCode = ErrorCode.valueOf(enumKey);
//        } catch (IllegalArgumentException e) {
//            //log.warn("Invalid error code: {}", enumKey);
//        }
//
//        return ResponseEntity.badRequest().body(
//                ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build());
//    }

    // thay đổi message này thành message khác
//    private String mapAttribute(String message, Map<String, Object> attribute){
//        String minValue = String.valueOf(attribute.get(MIN_ATTRIBUTE));
//        message = message.replace("{" + MIN_ATTRIBUTE + "}" , minValue);
//
//        String maxValue = String.valueOf(attribute.get(MAX_ATTRIBUTE));
//        message = message.replace("{" + MAX_ATTRIBUTE + "}" , maxValue);
//
//        return message;
//    }
    private String mapAttributes(String message, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();   // ví dụ: "min", "max", "value"
            Object value = entry.getValue(); // ví dụ: 4, 20, 1
            message = message.replace("{" + key + "}", value.toString());
        }
        return message;
    }


}
