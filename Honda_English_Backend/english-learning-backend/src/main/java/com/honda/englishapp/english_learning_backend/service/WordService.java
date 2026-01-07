package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.Word.WordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Word.WordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.WordCountResponse;
import com.honda.englishapp.english_learning_backend.dto.response.WordResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.Word;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.WordMapper;
import com.honda.englishapp.english_learning_backend.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WordService {
    WordRepository wordRepository;
    WordMapper wordMapper;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    LearnedWordRepository learnedWordRepository;
    UserLessonRepository userLessonRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public WordResponse createWord(WordCreationRequest request) {
        Word word = wordMapper.toWord(request);

        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!category.getCreatedBy().getId().equals(currentUser) || !user.getId().equals(currentUser))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        word.setCreatedBy(user);
        word.setCategory(category);

        return wordMapper.toWordResponse(wordRepository.save(word));
    }

    public WordResponse getActiveWord(Long id) {
        Word word = wordRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_EXISTED));

        return wordMapper.toWordResponse(word);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<WordResponse>> getActiveWordsByCategory(Long categoryId, int pageNo, int pageSize) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!userLessonRepository.existsByUserIdAndCategoryId(currentUser, categoryId))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        Page<Word> words = wordRepository.findByIsActiveTrueAndCategoryId(
                categoryId,
                PageRequest.of(pageNo - 1, pageSize,  Sort.by("englishWord"))
        );
        List<WordResponse> items = words.stream()
                .map(wordMapper::toWordResponse)
                .toList();

        return PageResponse.<List<WordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(words.getTotalPages())
                .totalElements(words.getTotalElements())
                .items(items)
                .build();
    }

    public PageResponse<List<WordResponse>> getActiveWordsBySubCategory(Long categoryId, int pageNo, int pageSize) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_A_SUB_CATEGORY));

        // Lấy danh sách word theo category đó, có phân trang
        Page<Word> words = wordRepository.findActiveWordsByActiveCategory(
                categoryId,
                PageRequest.of(pageNo - 1, pageSize, Sort.by("englishWord"))
        );

        List<WordResponse> items = words.stream()
                .map(wordMapper::toWordResponse)
                .toList();

        return PageResponse.<List<WordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(words.getTotalPages())
                .totalElements(words.getTotalElements())
                .items(items)
                .build();
    }



    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<WordResponse>> getActiveWords(int pageNo, int pageSize) {
        Page<Word> words = wordRepository.findByIsActiveTrue(
                PageRequest.of(pageNo - 1, pageSize , Sort.by("englishWord").ascending()));

        List<WordResponse> items = words.stream()
                .map(wordMapper::toWordResponse)
                .toList();

        return PageResponse.<List<WordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(words.getTotalPages())
                .totalElements(words.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("#userId == authentication.name")
    public PageResponse<List<WordResponse>> getActiveLearnedWordsByUserId(String userId, int pageNo, int pageSize) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Page<Word> words = learnedWordRepository.findWordsByUserIdAndIsActiveTrue(
                userId,
                PageRequest.of(pageNo - 1, pageSize, Sort.by("dateLearned").ascending())
        );
        List<WordResponse> items = words.stream()
                .map(wordMapper::toWordResponse)
                .toList();

        return PageResponse.<List<WordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(words.getTotalPages())
                .totalElements(words.getTotalElements())
                .items(items)
                .build();
    }

    public WordCountResponse countActiveWordsByParentCategory(Long categoryId) {
        Category category = categoryRepository.findByIdAndParentCategoryIdIsActiveTrue(categoryId, "admin")
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        long count = wordRepository.countByIsActiveTrueAndParentCategoryId(categoryId);

        return WordCountResponse.builder()
                .wordCount(count)
                .categoryId(categoryId)
                .categoryName(category.getName())
                .build();
    }

    public WordCountResponse countActiveWordsByCategoryId(Long categoryId) {
        Category category = categoryRepository.findByIdAndCategoryIdIsActiveTrue(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        long count = wordRepository.countByIsActiveTrueAndCategoryId(categoryId);

        return WordCountResponse.builder()
                .wordCount(count)
                .categoryId(categoryId)
                .categoryName(category.getName())
                .build();
    }

    public List<WordCountResponse> countActiveWordsForAllSystemGeneratedParentCategories() {

        return categoryRepository.findAllByParentIdIsNull()
                .stream()
                .map(category -> WordCountResponse.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .wordCount(wordRepository.countByIsActiveTrueAndParentCategoryId(category.getId()))
                        .build())
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public WordResponse updateWord(Long id, WordUpdateRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_EXISTED));

        wordMapper.patchWord(word, request);

        if (request.getCategoryId() != null) {
            word.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
        }

        return wordMapper.toWordResponse(wordRepository.save(word));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id))
            throw new AppException(ErrorCode.WORD_NOT_EXISTED);
        wordRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public void deactivateWord(Long id) {
        deactivateWords(List.of(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public void deactivateWords(List<Long> ids) {
        List<Word> words = wordRepository.findAllById(ids);

        if (words.size() != ids.size()) {
            throw new AppException(ErrorCode.WORD_NOT_EXISTED);
        }

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean allOwnedByUser = words.stream()
                .allMatch(word -> word.getCreatedBy().getId().equals(currentUserId));

        if (!allOwnedByUser && !isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        words.forEach(word -> word.setIsActive(false));
        wordRepository.saveAll(words);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<WordResponse>> searchWords(
            String englishWord,
            String vietnameseMeaning,
            Boolean isActive,
            Long categoryId,
            String sortBy,
            int pageNo,
            int pageSize) {

        List<Sort.Order> sorts = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()) {
                Sort.Direction direction = matcher.group(3).equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC : Sort.Direction.DESC;
                sorts.add(new Sort.Order(direction, matcher.group(1)));
            }
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sorts));
        Page<Word> page = wordRepository.searchWords(englishWord, vietnameseMeaning, isActive, categoryId, pageable);

        List<WordResponse> items = page.stream()
                .map(wordMapper::toWordResponse)
                .toList();

        return PageResponse.<List<WordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
