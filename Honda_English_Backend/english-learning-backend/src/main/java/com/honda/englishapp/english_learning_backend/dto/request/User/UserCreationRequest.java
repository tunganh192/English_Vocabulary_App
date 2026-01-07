package com.honda.englishapp.english_learning_backend.dto.request.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honda.englishapp.english_learning_backend.enums.Role;
import com.honda.englishapp.english_learning_backend.validator.DobConstrain;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotBlank(message = "USERNAME_REQUIRED")
    @Size(min = 4, max = 20, message = "USERNAME_LENGTH_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 6, max = 64, message = "PASSWORD_LENGTH_INVALID")
    String password;
    // Phù hợp với BCrypt, vốn xử lý tốt tới 72 ký tự (quá dài cũng không tăng tính bảo mật).

    @Size(min = 1, max = 50, message = "DISPLAY_NAME_LENGTH_INVALID")
    @NotBlank(message = "DISPLAY_NAME_REQUIRED")
    String displayName;

    Role role;

    Integer dailyGoal;

    @NotNull(message = "DOB_REQUIRED")
    @DobConstrain(min = 6, message = "INVALID_DOB")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth; // can sua

}
