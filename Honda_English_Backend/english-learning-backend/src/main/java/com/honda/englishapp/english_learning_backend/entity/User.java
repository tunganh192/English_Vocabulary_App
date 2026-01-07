package com.honda.englishapp.english_learning_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "display_name")
    String displayName ;

    @Column(name = "role", columnDefinition = "VARCHAR(50) DEFAULT 'USER'")
    String role = "USER"; // Ví dụ: "ROLE_USER", "ROLE_ADMIN"

    @Column(name = "daily_goal", columnDefinition = "INT DEFAULT 1")
    Integer dailyGoal = 1;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @JsonManagedReference
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    List<Category> createdCategories;

    @JsonManagedReference
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    List<Word> createdWords;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<LearnedWord> learnedWords;

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Reminder reminder;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    List<InvalidatedToken> invalidatedTokens;

    @PrePersist
    public void prePersist() {
        // Kiểm tra nếu role và dailyGoal không được set (null), thì gán giá trị mặc định
        if (this.role == null) {
            this.role = "USER";
        }
        if (this.dailyGoal == null) {
            this.dailyGoal = 0;
        }
    }
}
