package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
import com.honda.englishapp.english_learning_backend.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearnedWordRepository extends JpaRepository<LearnedWord, Long>, JpaSpecificationExecutor<LearnedWord> {

    // Tìm kiếm nhiều giá trị
//    @Query(value = """
//        SELECT lw FROM LearnedWord lw
//        WHERE (:userId IS NULL OR lw.user.id = :userId)
//          AND (:wordId IS NULL OR lw.word.id = :wordId)
//          AND (:isMastered IS NULL OR lw.isMastered = :isMastered)
//          AND (:correctStreak IS NULL OR lw.correctStreak = :correctStreak)
//          AND (:dateLearned IS NULL OR FUNCTION('DATE', lw.dateLearned) = :dateLearned)
//        """,
//            countQuery = """
//        SELECT COUNT(lw) FROM LearnedWord lw
//        WHERE (:userId IS NULL OR lw.user.id = :userId)
//          AND (:wordId IS NULL OR lw.word.id = :wordId)
//          AND (:isMastered IS NULL OR lw.isMastered = :isMastered)
//          AND (:correctStreak IS NULL OR lw.correctStreak = :correctStreak)
//          AND (:dateLearned IS NULL OR FUNCTION('DATE', lw.dateLearned) = :dateLearned)
//        """)
//    Page<LearnedWord> searchLearnedWords(@Param("userId") String userId,
//                                         @Param("wordId") Long wordId,
//                                         @Param("isMastered") Boolean isMastered,
//                                         @Param("correctStreak") Integer correctStreak,
//                                         @Param("dateLearned") LocalDate dateLearned,
//                                         Pageable pageable);
    boolean existsByUserIdAndWordId(String userId, Long wordId);



    //Truy vấn bắt đầu từ LearnedWord để lọc theo userId, sau đó join với Word để kiểm tra isActive = true. Điều này hiệu quả hơn so với bắt đầu từ Word và left join với LearnedWord.
    //LearnedWordRepository phù hợp với logic liên quan đến mối quan hệ User-Word.
    //Hàm findWordsByUserIdAndIsActiveTrue trả về Page<Word>, cho phép WordService ánh xạ thành WordResponse.
//    @Query("SELECT lw.word FROM LearnedWord lw WHERE lw.user.id = :userId AND lw.word.isActive = true")
//    Page<Word> findWordsByUserIdAndIsActiveTrue(@Param("userId") String userId, Pageable pageable);
    @Query("SELECT lw.word FROM LearnedWord lw WHERE lw.user.id = :userId AND lw.word.isActive = true AND lw.isMastered = true")
    Page<Word> findWordsByUserIdAndIsActiveTrue(@Param("userId") String userId, Pageable pageable);

    // Kiem tra de cap nhat tu sau khi tra lois
    Optional<LearnedWord> findByUserIdAndWordId(String userId, Long wordId);

    // bo tu co master = true khỏi cau hoi
    @Query("SELECT lw.word.id FROM LearnedWord lw WHERE lw.user.id = :userId AND lw.isMastered = true")
    List<Long> findMasteredWordIdsByUserId(String userId);



    @Query(value = """
    SELECT * FROM learned_word lw
    WHERE (:userId IS NULL OR lw.user_id = :userId)
      AND (:wordId IS NULL OR lw.word_id = :wordId)
      AND (:isMastered IS NULL OR lw.is_mastered = :isMastered)
      AND (:correctStreak IS NULL OR lw.correct_streak = :correctStreak)
      AND (:dateLearned IS NULL OR DATE(lw.date_learned) = :dateLearned)
    """,
            countQuery = """
    SELECT COUNT(*) FROM learned_word lw
    WHERE (:userId IS NULL OR lw.user_id = :userId)
      AND (:wordId IS NULL OR lw.word_id = :wordId)
      AND (:isMastered IS NULL OR lw.is_mastered = :isMastered)
      AND (:correctStreak IS NULL OR lw.correct_streak = :correctStreak)
      AND (:dateLearned IS NULL OR DATE(lw.date_learned) = :dateLearned)
    """,
            nativeQuery = true)
    Page<LearnedWord> searchLearnedWords(@Param("userId") String userId,
                                         @Param("wordId") Long wordId,
                                         @Param("isMastered") Boolean isMastered,
                                         @Param("correctStreak") Integer correctStreak,
                                         @Param("dateLearned") LocalDate dateLearned,
                                         Pageable pageable);

    //FUNCTION('DATE', lw.dateLearned) giúp chỉ lấy phần ngày (bỏ giờ), dùng tốt với MySQL hoặc H2.



    @Query("SELECT COUNT(lw) FROM LearnedWord lw WHERE lw.user.id = :userId AND lw.word.category.id = :categoryId AND lw.word.isActive = true AND lw.isMastered = true")
    long countByUserIdAndCategoryIdAndWordIsActiveTrueAndIsMasteredTrue(@Param("userId") String userId, @Param("categoryId") Long categoryId);

    // tong so tu da hoc cua user
    Long countByUserId(String userId);


    // tính tổng từ đã học theo thời gian
    @Query(value = "SELECT COUNT(*) " +
            "FROM learned_word " +
            "WHERE DATE(date_learned) = DATE(:date) " +
            "AND user_id = :userId AND is_mastered = true",
            nativeQuery = true)
    Long countByUserIdAndDay(@Param("userId") String userId, @Param("date") Date date);

    @Query(value = "SELECT COUNT(*) " +
            "FROM learned_word " +
            "WHERE YEARWEEK(date_learned, 1) = YEARWEEK(:date, 1) " +
            "AND user_id = :userId AND is_mastered = true",
            nativeQuery = true)
    Long countByUserIdAndWeek(@Param("userId") String userId, @Param("date") Date date);

    @Query(value = "SELECT COUNT(*) " +
            "FROM learned_word " +
            "WHERE user_id = :userId " +
            "AND YEAR(date_learned) = YEAR(:date) " +
            "AND MONTH(date_learned) = MONTH(:date) AND is_mastered = true",
            nativeQuery = true)
    Long countByUserIdAndMonth(@Param("userId") String userId, @Param("date") Date date);

    @Query(value = "SELECT COUNT(*) " +
            "FROM learned_word " +
            "WHERE user_id = :userId " +
            "AND YEAR(date_learned) = YEAR(:date) AND is_mastered = true",
            nativeQuery = true)
    Long countByUserIdAndYear(@Param("userId") String userId, @Param("date") Date date);


    // Tính chi tiết thời gian theo tuần, tháng , năm
    @Query(value = "SELECT DAYNAME(date_learned) AS dayname, COUNT(*) AS word_count " +
            "FROM learned_word " +
            "WHERE user_id = :userId " +
            "AND YEARWEEK(date_learned, 1) = YEARWEEK(:date, 1) AND is_mastered = true " +
            "GROUP BY DAYNAME(date_learned), DATE(date_learned) " +
            "ORDER BY DAYNAME(date_learned)",
            nativeQuery = true)
    List<Object[]> countWordsByDayInWeek(@Param("userId") String userId, @Param("date") Date date);

    @Query(value = "SELECT WEEK(date_learned, 1) - WEEK(DATE_FORMAT(date_learned, '%Y-%m-01'), 1) + 1 AS week_in_month, " +
            "COUNT(*) AS word_count " +
            "FROM learned_word " +
            "WHERE user_id = :userId " +
            "AND YEAR(date_learned) = YEAR(:date) " +
            "AND MONTH(date_learned) = MONTH(:date) AND is_mastered = true " +
            "GROUP BY week_in_month " +
            "ORDER BY week_in_month",
            nativeQuery = true)
    List<Object[]> countWordsByWeekInMonth(@Param("userId") String userId, @Param("date") Date date);

    @Query(value = "SELECT MONTH(date_learned) AS month, COUNT(*) AS word_count " +
            "FROM learned_word " +
            "WHERE user_id = :userId " +
            "AND YEAR(date_learned) = YEAR(:date) AND is_mastered = true " +
            "GROUP BY MONTH(date_learned) " +
            "ORDER BY month",
            nativeQuery = true)
    List<Object[]> countWordsByMonthInYear(@Param("userId") String userId, @Param("date") Date date);

    @Query("SELECT lw FROM LearnedWord lw WHERE lw.user.id = :userId AND lw.word.category.id = :categoryId AND lw.word.isActive = true")
    List<LearnedWord> findAllByUserIdAndWordCategoryId(@Param("userId") String userId, @Param("categoryId") Long categoryId);
}

