package com.honda.englishapp.english_learning_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "word")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "english_word", nullable = false)
    String englishWord;

    @Column(name = "pronunciation")
    String pronunciation;

    @Column(name = "vietnamese_meaning", nullable = false)
    String vietnameseMeaning;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "created_by")
    User createdBy;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    List<LearnedWord> learnedWords;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

}
