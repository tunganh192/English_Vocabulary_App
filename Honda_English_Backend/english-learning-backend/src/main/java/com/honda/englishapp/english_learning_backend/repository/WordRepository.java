package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>, JpaSpecificationExecutor<Word> {
    //List<Word> findByCategoryId(Long categoryId);

    // Thêm phương thức để tìm tất cả các từ thuộc danh sách categoryIds
    List<Word> findByCategoryIdIn(List<Long> categoryIds);

//    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.vietnameseMeaning != :correctMeaning ORDER BY RAND() LIMIT 1")
//    Optional<String> findRandomWrongMeaningGlobal(@Param("correctMeaning") String correctMeaning);

    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id != :excludeWordId ORDER BY FUNCTION('RAND')")
    List<String> findRandomVietnameseMeaningsAndNotWordId(
            @Param("excludeWordId") Long excludeWordId,
            Pageable pageable
    );


    @Query(value = """
        SELECT w FROM Word w
        WHERE (:englishWord IS NULL OR w.englishWord LIKE %:englishWord%)
          AND (:vietnameseMeaning IS NULL OR w.vietnameseMeaning LIKE %:vietnameseMeaning%)
          AND (:isActive IS NULL OR w.isActive = :isActive)
          AND (:categoryId IS NULL OR w.category.id = :categoryId)
        """,
            countQuery = """
        SELECT COUNT(w) FROM Word w
        WHERE (:englishWord IS NULL OR w.englishWord LIKE %:englishWord%)
          AND (:vietnameseMeaning IS NULL OR w.vietnameseMeaning LIKE %:vietnameseMeaning%)
          AND (:isActive IS NULL OR w.isActive = :isActive)
          AND (:categoryId IS NULL OR w.category.id = :categoryId)
        """)
    Page<Word> searchWords(@Param("englishWord") String englishWord,
                           @Param("vietnameseMeaning") String vietnameseMeaning,
                           @Param("isActive") Boolean isActive,
                           @Param("categoryId") Long categoryId,
                           Pageable pageable);

    Page<Word> findByIsActiveTrue(Pageable pageable);

    Page<Word> findByIsActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT w FROM Word w " +
            "WHERE w.isActive = true " +
            "AND w.category.id = :categoryId " +
            "AND w.category.isActive = true")
    Page<Word> findActiveWordsByActiveCategory(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    Optional<Word> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT COUNT(w) FROM Word w WHERE w.isActive = true AND w.category.id IN (SELECT c.id FROM Category c WHERE c.parentId = :categoryId)")
    long countByIsActiveTrueAndParentCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(w) FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId")
    long countByIsActiveTrueAndCategoryId(@Param("categoryId") Long categoryId);



    @Query(value = "SELECT * FROM word WHERE is_active = true AND category_id = :categoryId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Word> findRandomByIsActiveTrueAndCategoryId(@Param("categoryId") Long categoryId);

    // THAY ĐỔI: Thêm để đếm từ hợp lệ, loại từ đã mastered
    @Query("SELECT COUNT(w) FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId AND w.id NOT IN :masteredWordIds")
    long countByIsActiveTrueAndCategoryIdAndNotMastered(@Param("categoryId") Long categoryId, @Param("masteredWordIds") List<Long> masteredWordIds);

    // Tạo câu hỏi
    // Tim tu ngau nhien de lam cau hoi, loại từ đã mastered
    @Query("SELECT w FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId AND w.id NOT IN :masteredWordIds ORDER BY RAND() LIMIT 1")
    Optional<Word> findRandomByIsActiveTrueAndCategoryIdAndNotMastered(@Param("categoryId") Long categoryId, @Param("masteredWordIds") List<Long> masteredWordIds);

    @Query("SELECT w FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId AND w.id IN :wordIds ORDER BY RAND() LIMIT 1")
    Optional<Word> findRandomByIsActiveTrueAndCategoryIdAndInWordIds(Long categoryId, List<Long> wordIds);


    // Random nghia cho true false
    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id != :excludeWordId AND w.category.id = :categoryId ORDER BY RAND() LIMIT 1")
    Optional<String> findRandomVietnameseMeaningByCategoryIdAndNotWordId(@Param("categoryId") Long categoryId, @Param("excludeWordId") Long excludeWordId);

    // Tạo câu hỏi 4 đáp án
    // THAY ĐỔI: Thay thế findThreeRandomVietnameseMeaningsByCategoryIdAndNotWordId
    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId AND w.id != :excludeWordId ORDER BY RAND() LIMIT 3")
    List<String> findThreeRandomVietnameseMeaningsByCategoryIdAndNotWordId(@Param("categoryId") Long categoryId, @Param("excludeWordId") Long excludeWordId);

    // 6 đáp án
    // THAY ĐỔI: Thay thế findFiveRandomVietnameseMeaningsByCategoryIdAndNotWordIds để loại từ đã mastered
    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id != :excludeWordIds AND w.category.id = :categoryId ORDER BY RAND() LIMIT 5")
    List<String> findFiveRandomVietnameseMeaningsByCategoryIdAndNotWordIds(@Param("categoryId") Long categoryId, @Param("excludeWordIds") Long excludeWordIds);

    // Ghép từ
    // THAY ĐỔI: Sửa findRandomEnglishWordsByCategoryIdAndNotWordIds để thêm masteredWordIds
    //@Query("SELECT w.englishWord FROM Word w WHERE w.isActive = true AND w.id NOT IN :excludeWordIds AND w.category.id = :categoryId AND w.id NOT IN :masteredWordIds ORDER BY RAND() LIMIT :limit")
    //List<String> findRandomEnglishWordsByCategoryIdAndNotWordIds(@Param("categoryId") Long categoryId, @Param("excludeWordIds") List<Long> excludeWordIds, @Param("masteredWordIds") List<Long> masteredWordIds, @Param("limit") int limit);

    // Tìm từ ngẫu nhiên để trộn
    // THAY ĐỔI: Thay thế findRandomEnglishWordByCategoryIdAndNotWordId để loại từ đã mastered
    @Query("SELECT w.englishWord FROM Word w WHERE w.isActive = true AND w.id != :excludeWordId AND w.category.id = :categoryId AND w.id NOT IN :masteredWordIds ORDER BY RAND() LIMIT 1")
    Optional<String> findRandomEnglishWordByCategoryIdAndNotWordIdAndNotMastered(@Param("categoryId") Long categoryId, @Param("excludeWordId") Long excludeWordId, @Param("masteredWordIds") List<Long> masteredWordIds);

//    //Tao cau hoi true false
//    @Query("SELECT w FROM Word w WHERE w.isActive = true AND w.category.id = :categoryId ORDER BY RAND() LIMIT 1")
//    Optional<Word> findRandomByIsActiveTrueAndCategoryId(@Param("categoryId") Long categoryId);
//
//    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id != :excludeWordId AND w.category.id = :categoryId ORDER BY RAND() LIMIT 1")
//    Optional<String> findRandomVietnameseMeaningByCategoryIdAndNotWordId(@Param("categoryId") Long categoryId, @Param("excludeWordId") Long excludeWordId);
//
//    // tao cau hoi 4 dap an
//    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id NOT IN :excludeWordIds AND w.category.id = :categoryId ORDER BY RAND() LIMIT 3")
//    List<String> findThreeRandomVietnameseMeaningsByCategoryIdAndNotWordIds(@Param("categoryId") Long categoryId, @Param("excludeWordIds") List<Long> excludeWordIds);
//
//    // 6 dap an
//    @Query("SELECT w.vietnameseMeaning FROM Word w WHERE w.isActive = true AND w.id NOT IN :excludeWordIds AND w.category.id = :categoryId ORDER BY RAND() LIMIT 5")
//    List<String> findFiveRandomVietnameseMeaningsByCategoryIdAndNotWordIds(@Param("categoryId") Long categoryId, @Param("excludeWordIds") List<Long> excludeWordIds);
//
//    // ghep tu
//    @Query("SELECT w.englishWord FROM Word w WHERE w.isActive = true AND w.id NOT IN :excludeWordIds AND w.category.id = :categoryId ORDER BY RAND() LIMIT :limit")
//    List<String> findRandomEnglishWordsByCategoryIdAndNotWordIds(@Param("categoryId") Long categoryId, @Param("excludeWordIds") List<Long> excludeWordIds, @Param("limit") int limit);
//
//    // tim tu nghau nhien de tron
//    @Query("SELECT w.englishWord FROM Word w WHERE w.isActive = true AND w.id != :excludeWordId AND w.category.id = :categoryId ORDER BY RAND() LIMIT 1")
//    Optional<String> findRandomEnglishWordByCategoryIdAndNotWordId(@Param("categoryId") Long categoryId, @Param("excludeWordId") Long excludeWordId);
}
