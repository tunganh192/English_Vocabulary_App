package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.entity.Reminder;
import com.honda.englishapp.english_learning_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    boolean existsByUserId(String userId);

    Optional<Reminder> findByUserId(String userId);

}
