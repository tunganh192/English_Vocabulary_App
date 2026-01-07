package com.honda.englishapp.english_learning_backend.repository;

import com.honda.englishapp.english_learning_backend.entity.Category;
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
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("""
        SELECT c FROM Category c
        WHERE (:code IS NULL OR c.code LIKE %:code%)
          AND (:name IS NULL OR c.name LIKE %:name%)
          AND c.isActive = TRUE
    """)
    Page<Category> searchLessons(@Param("name") String name,
                                          @Param("code") String code,
                                          Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentId IS NOT NULL AND c.createdBy.id = :userId AND c.isActive = TRUE")
    Page<Category> findSubCategoriesCreatedByAdmin(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentId IS NULL AND c.createdBy.id = :userId AND c.isActive = TRUE")
    Page<Category> findByParentIsNullAndCreatedByUserId(@Param("userId") String userId, Pageable pageable);

    Page<Category> findByParentIdAndIsActiveTrue(Long parentId, Pageable pageable);

    Page<Category> findByCreatedByIdAndIsActiveTrue(String createdBy, Pageable pageable);

    Optional<Category> findByCode(String code);

    //Optional<Category> findByIdAndParentIdIsNull(Long id);
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isActive = TRUE AND c.parentId IS NULL AND c.createdBy.id = :userId")
    Optional<Category> findByIdAndParentCategoryIdIsActiveTrue(@Param("id") Long id, @Param("userId") String userId);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isActive = TRUE ")
    Optional<Category> findByIdAndCategoryIdIsActiveTrue(@Param("id") Long id);

    @Query("SELECT c FROM Category c WHERE c.parentId IS NULL")
    List<Category> findAllByParentIdIsNull();

    boolean existsByCode(String code);


}
