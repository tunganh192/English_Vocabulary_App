package com.honda.englishapp.english_learning_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honda.englishapp.english_learning_backend.enums.RepeatType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Entity
@Table(name = "reminder")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;

    @Column(name = "time")
    @JsonFormat(pattern = "HH:mm")
    LocalTime time;

    @Column(name = "repeat_type")
    RepeatType repeatType; // Ví dụ: "daily", "weekly", "monthly"

    @Column(name = "repeat_interval")
    @JsonFormat(pattern = "HH:mm")
    LocalTime repeatInterval;

    @Column(name = "is_enabled")
    Boolean isEnabled;

    @PrePersist
    public void prePersist() {
        if (this.isEnabled == null) {
            this.isEnabled = true;
        }
    }
}
