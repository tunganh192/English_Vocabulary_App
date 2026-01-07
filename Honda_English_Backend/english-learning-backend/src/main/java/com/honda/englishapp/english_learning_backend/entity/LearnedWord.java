package com.honda.englishapp.english_learning_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "learned_word",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "word_id"})
)

public class LearnedWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "word_id", nullable = false)
    Word word;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_learned")
    Date dateLearned;

    @Column(name = "correct_count")
    int correctCount;

    @Column(name = "wrong_count")
    int wrongCount;

    @Column(name = "correct_streak")
    int correctStreak;

    @Column(name = "is_mastered")
    boolean isMastered;


}
