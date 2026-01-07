package com.honda.englishapp.english_learning_backend.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGRIED_EXCEPTION(9999,"Uncategried exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Key Invalid", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User existed", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(1003,"User not existed", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1004,"Username at least {min} character", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005,"Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006,"You do not have permission", HttpStatus.FORBIDDEN),

    USERNAME_REQUIRED(1007, "Username must not be blank", HttpStatus.BAD_REQUEST),
    USERNAME_LENGTH_INVALID(1008, "Username must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),

    PASSWORD_REQUIRED(1009, "Password must not be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH_INVALID(1010, "Password must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),

    DISPLAY_NAME_REQUIRED(1011, "Display name must not be blank", HttpStatus.BAD_REQUEST),
    DISPLAY_NAME_LENGTH_INVALID(1012, "Display name must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),


    ROLE_REQUIRED(1013, "Role must not be blank", HttpStatus.BAD_REQUEST),

    DAILY_GOAL_REQUIRED(1014, "Daily goal must not be null", HttpStatus.BAD_REQUEST),
    DAILY_GOAL_MIN(1015, "Daily goal must be at least {value}", HttpStatus.BAD_REQUEST),

    INVALID_DOB(1016, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    DOB_REQUIRED(1017, "Date of birth must not be null", HttpStatus.BAD_REQUEST), //can sua

    PATCH_REQUEST_EMPTY(1018, "PATCH request cannot be empty", HttpStatus.BAD_REQUEST),

    PAGE_NO_REQUIRED(1019, "Page number is required", HttpStatus.BAD_REQUEST),
    PAGE_SIZE_REQUIRED(1020, "Page size is required", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_A_PARENT(1021, "Category is not a parent category", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_WORDS(1022, "Not enough words to generate question", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_WRONG_MEANINGS(1023, "Not enough wrong meanings to generate question", HttpStatus.BAD_REQUEST),
    WORD_TOO_SHORT(1024, "Word is too short to split", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_DISTRACTORS(1025, "Not enough distractor words to generate question", HttpStatus.BAD_REQUEST),
    QUESTION_NOT_FOUND(1026, "Question not found", HttpStatus.BAD_REQUEST),
    INVALID_QUESTION_TYPE(1027, "Invalid question type", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1028, "Token has expired", HttpStatus.UNAUTHORIZED), // bạn đã có sẵn, giữ lại
    TOKEN_INVALID(1029, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1030, "Token has been revoked", HttpStatus.UNAUTHORIZED),
    NO_MASTERED_WORDS(1031,"Not enough master words to generate question",HttpStatus.BAD_REQUEST),

    //User
    USERNAME_EXISTED(1032,"Username existed", HttpStatus.CONFLICT),



    //CATEGORY
    CATEGORY_NOT_EXISTED(2001,"Category not existed", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(2002,"Category existed", HttpStatus.CONFLICT),
    CATEGORY_NOT_A_SUB_CATEGORY(2011, "Category not a  substitute category", HttpStatus.NOT_FOUND),
    CATEGORY_CODE_EXISTED(2012,"Category code existed", HttpStatus.CONFLICT),



    CATEGORY_NAME_REQUIRED(2003, "Category name must not be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_LENGTH_INVALID(2004, "Category name must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    ICON_URL_LENGTH_INVALID(2005, "Icon URL must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    PARENT_ID_MUST_BE_POSITIVE(2006, "Parent ID must be greater than 0", HttpStatus.BAD_REQUEST),
    CATEGORY_CODE_REQUIRED(2007, "Category code must not be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_CODE_LENGTH_INVALID(2008, "Category code must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    CREATED_BY_REQUIRED(2009, "Created by must not be blank", HttpStatus.BAD_REQUEST),
    CREATED_BY_LENGTH_INVALID(2010, "Created by must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),

    //WORD
    WORD_NOT_EXISTED(3001, "Word not existed", HttpStatus.NOT_FOUND),
    WORD_EXISTED(3002, "Word existed", HttpStatus.CONFLICT),
    WORD_ID_REQUIRED(3003, "Word ID must not be null", HttpStatus.BAD_REQUEST),

    ENGLISH_WORD_REQUIRED(3004, "English word must not be empty", HttpStatus.BAD_REQUEST),
    ENGLISH_WORD_LENGTH_INVALID(3005, "English word must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    PRONUNCIATION_LENGTH_INVALID(3006, "Pronunciation must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    VIETNAMESE_MEANING_REQUIRED(3007, "Vietnamese meaning must not be blank", HttpStatus.BAD_REQUEST),
    VIETNAMESE_MEANING_LENGTH_INVALID(3008, "Vietnamese meaning must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    CATEGORY_ID_REQUIRED(3009, "Category ID must not be null", HttpStatus.BAD_REQUEST),




    //LearnedWord
    LEARNED_WORD_NOT_EXISTED(4001, "LearnedWord not existed", HttpStatus.NOT_FOUND),
    LEARNED_WORD_EXISTED(4002, "LearnedWord existed", HttpStatus.CONFLICT),

    USER_ID_REQUIRED(4003, "User ID must not be blank", HttpStatus.BAD_REQUEST),
    WORD_ID_POSITIVE(4004, "Word ID must be a positive number", HttpStatus.BAD_REQUEST),

    CORRECT_COUNT_MIN(4005, "Correct count must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    WRONG_COUNT_MIN(4006, "Wrong count must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    CORRECT_STREAK_MIN(4007, "Correct streak must be greater than or equal to 0", HttpStatus.BAD_REQUEST),

    //Reminder
    REMINDER_NOT_EXISTED(5001, "Reminder not existed", HttpStatus.NOT_FOUND),
    REMINDER_EXISTED(5002, "Reminder existed", HttpStatus.CONFLICT),

    TIME_REQUIRED(5003, "Time must not be null", HttpStatus.BAD_REQUEST),
    REPEAT_TYPE_REQUIRED(5004, "Repeat type must not be null", HttpStatus.BAD_REQUEST),
    //IS_ENABLED_REQUIRED(5003, "Enabled status must not be null", HttpStatus.BAD_REQUEST),

    //user lesson
    USER_ALREADY_JOINED_CATEGORY(6001, "User has already joined this category", HttpStatus.BAD_REQUEST),
    USER_LESSON_NOT_EXISTED(6002, "User-lesson not existed", HttpStatus.BAD_REQUEST),


    //static
    DATE_REQUIRED(7002, "Date must not be null", HttpStatus.BAD_REQUEST),

    //token
    TOKEN_REQUIRED(8001, "Token must not be blank", HttpStatus.BAD_REQUEST),

    ;

    int code;
    String message;
    HttpStatus httpStatusCode;

    ErrorCode(int code, String message,HttpStatus httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
