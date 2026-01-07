package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.UserLesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLesson, Long> {
    boolean existsByUserIdAndCategoryId(String userId, Long categoryId);
    void deleteByUserIdAndCategoryId(String userId, Long categoryId);

    Optional<UserLesson> findByUserIdAndCategoryId(String userId, Long categoryId);


    @Query("SELECT ul.category FROM UserLesson ul WHERE ul.user.id = :userId")
    Page<Category> findCategoriesByUserId(@Param("userId") String userId, Pageable pageable);

    Page<UserLesson> findByUserId(String userId, Pageable pageable);

    Page<UserLesson> findByCategoryId(Long categoryId, Pageable pageable);



}
