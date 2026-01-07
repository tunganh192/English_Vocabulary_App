package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT u FROM User u
            WHERE (:username IS NULL OR u.username LIKE %:username%)
              AND (:displayName IS NULL OR u.displayName LIKE %:displayName%)
              AND (:role IS NULL OR u.role = :role)
              AND (:dailyGoal IS NULL OR u.dailyGoal = :dailyGoal)
              AND (:dateOfBirth IS NULL OR u.dateOfBirth = :dateOfBirth)
            """,
            countQuery = """
            SELECT COUNT(u) FROM User u
            WHERE (:username IS NULL OR u.username LIKE %:username%)
              AND (:displayName IS NULL OR u.displayName LIKE %:displayName%)
              AND (:role IS NULL OR u.role = :role)
              AND (:dailyGoal IS NULL OR u.dailyGoal = :dailyGoal)
              AND (:dateOfBirth IS NULL OR u.dateOfBirth = :dateOfBirth)
            """)
    Page<User> searchUsers(@Param("username") String username,
                           @Param("displayName") String displayName,
                           @Param("role") String role,
                           @Param("dailyGoal") Integer dailyGoal,
                           @Param("dateOfBirth") LocalDate dateOfBirth,
                           Pageable pageable);


    @Query("SELECT DISTINCT u FROM User u JOIN u.learnedWords lw WHERE lw.word.category.id = :categoryId AND lw.word.isActive = true")
    Page<User> findStudentsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
//    @Query("select * from user u where")
//    List<User> findUserByLastName();
//
//    @Query(value = "select u from User u where u.username = :username and u.dob = :dob" , nativeQuery = true)
//    Optional<User> findByUsername(@Param("username") String username, @Param("dob") LocalDate dob);

}
