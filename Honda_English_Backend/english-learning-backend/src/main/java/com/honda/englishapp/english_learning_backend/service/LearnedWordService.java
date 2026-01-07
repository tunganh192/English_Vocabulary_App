package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.LearnedWordResponse;
import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.LearnedWordPercentageResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.Word;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.mapper.LearnedWordMapper;
import com.honda.englishapp.english_learning_backend.repository.CategoryRepository;
import com.honda.englishapp.english_learning_backend.repository.LearnedWordRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import com.honda.englishapp.english_learning_backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LearnedWordService {

    LearnedWordRepository learnedWordRepository;
    LearnedWordMapper learnedWordMapper;
    UserRepository userRepository;
    WordRepository wordRepository;
    CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.name")
    public LearnedWordResponse create(LearnedWordCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_EXISTED));

        if (learnedWordRepository.existsByUserIdAndWordId(request.getUserId(), request.getWordId())) {
            throw new AppException(ErrorCode.LEARNED_WORD_EXISTED);
        }

        LearnedWord learnedWord = LearnedWord.builder()
                .user(user)
                .word(word)
                .build();

        return learnedWordMapper.toLearnedWordResponse(learnedWordRepository.save(learnedWord));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<LearnedWordResponse>> getAll(int pageNo, int pageSize) {
        Page<LearnedWord> learnedWords = learnedWordRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
        List<LearnedWordResponse> items = learnedWords.stream()
                .map(learnedWordMapper::toLearnedWordResponse)
                .toList();

        return PageResponse.<List<LearnedWordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(learnedWords.getTotalPages())
                .totalElements(learnedWords.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<List<LearnedWordResponse>> searchLearnedWords(
            String userId,
            Long wordId,
            Boolean isMastered,
            Integer correctStreak,
            LocalDate dateLearned,
            String sortBy,
            int pageNo,
            int pageSize) {

        List<Sort.Order> orders = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()) {
                Sort.Direction direction = matcher.group(3).equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC : Sort.Direction.DESC;
                orders.add(new Sort.Order(direction, matcher.group(1)));
            }
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(orders));
        Page<LearnedWord> page = learnedWordRepository.searchLearnedWords(
                userId, wordId, isMastered, correctStreak, dateLearned, pageable
        );

        List<LearnedWordResponse> items = page.stream()
                .map(learnedWordMapper::toLearnedWordResponse)
                .toList();

        return PageResponse.<List<LearnedWordResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LearnedWordResponse getById(Long id) {
        return learnedWordMapper.toLearnedWordResponse(
                learnedWordRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.LEARNED_WORD_NOT_EXISTED))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LearnedWordResponse update(Long id, LearnedWordUpdateRequest request) {
        LearnedWord learnedWord = learnedWordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LEARNED_WORD_NOT_EXISTED));

        learnedWordMapper.patchLearnedWord(learnedWord, request);
        return learnedWordMapper.toLearnedWordResponse(learnedWordRepository.save(learnedWord));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        if (!learnedWordRepository.existsById(id)) {
            throw new AppException(ErrorCode.LEARNED_WORD_NOT_EXISTED);
        }
        learnedWordRepository.deleteById(id);
    }

    public LearnedWordPercentageResponse calculateLearnedWordPercentageByCategory(String userId, Long categoryId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        long totalCount = wordRepository.countByIsActiveTrueAndCategoryId(categoryId);
        long learnedCount = learnedWordRepository.countByUserIdAndCategoryIdAndWordIsActiveTrueAndIsMasteredTrue(userId, categoryId);

        double percentage = totalCount == 0 ? 0.0 : Math.round(((double) learnedCount / totalCount) * 1000) / 10.0;

        return LearnedWordPercentageResponse.builder()
                .percentage(percentage)
                .categoryId(categoryId)
                .categoryName(category.getName())
                .learnedCount(learnedCount)
                .totalCount(totalCount)
                .build();
    }
}
