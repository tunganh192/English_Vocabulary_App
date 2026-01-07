package com.honda.englishapp.english_learning_backend.service;


import com.honda.englishapp.english_learning_backend.dto.response.PageResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.LearnedWordAccuracyResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.WordsLearnedStatsResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Statistics.TotalWordsLearnedResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.repository.CategoryRepository;
import com.honda.englishapp.english_learning_backend.repository.LearnedWordRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
    LearnedWordRepository learnedWordRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public LearnedWordAccuracyResponse getLearnedWordAccuracy(String userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Tính tổng số từ đã học thuộc (isMastered = true)
        long learnedCount = learnedWordRepository.countByUserIdAndCategoryIdAndWordIsActiveTrueAndIsMasteredTrue(userId, categoryId);

        // Tính correctCount và totalCount từ LearnedWord
        long correctCount = learnedWordRepository.findAllByUserIdAndWordCategoryId(userId, categoryId)
                .stream()
                .mapToLong(LearnedWord::getCorrectCount)
                .sum();

        long totalCount = learnedWordRepository.findAllByUserIdAndWordCategoryId(userId, categoryId)
                .stream()
                .mapToLong(lw -> lw.getCorrectCount() + lw.getWrongCount())
                .sum();

        return LearnedWordAccuracyResponse.builder()
                .userId(userId)
                .displayName(user.getDisplayName())
                .categoryId(categoryId)
                .categoryName(category.getName())
                .learnedCount(learnedCount)
                .correctCount(correctCount)
                .totalCount(totalCount)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public PageResponse<List<LearnedWordAccuracyResponse>> getCategoryAccuracyStats(Long categoryId, int pageNo, int pageSize) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isOwner = category.getCreatedBy().getId().equals(currentUser);

        if (!isAdmin() && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy danh sách học sinh thuộc category với phân trang
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<User> studentPage = userRepository.findStudentsByCategory(categoryId, pageable);

        // Tính thống kê cho từng học sinh
        List<LearnedWordAccuracyResponse> statsList = studentPage.getContent().stream().map(student -> {
            String studentId = student.getId();
            // Tính số từ đã học thuộc (isMastered = true)
            long learnedCount = learnedWordRepository.countByUserIdAndCategoryIdAndWordIsActiveTrueAndIsMasteredTrue(studentId, categoryId);

            // Tính correctCount và totalCount từ LearnedWord
            List<LearnedWord> learnedWords = learnedWordRepository.findAllByUserIdAndWordCategoryId(studentId, categoryId);
            long correctCount = learnedWords.stream()
                    .mapToLong(LearnedWord::getCorrectCount)
                    .sum();
            long totalCount = learnedWords.stream()
                    .mapToLong(lw -> lw.getCorrectCount() + lw.getWrongCount())
                    .sum();

            return LearnedWordAccuracyResponse.builder()
                    .userId(studentId)
                    .displayName(student.getDisplayName())
                    .categoryId(categoryId)
                    .categoryName(category.getName())
                    .learnedCount(learnedCount)
                    .correctCount(correctCount)
                    .totalCount(totalCount)
                    .build();
        }).collect(Collectors.toList());

        // Tạo phản hồi phân trang
        return PageResponse.<List<LearnedWordAccuracyResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(studentPage.getTotalPages())
                .totalElements(studentPage.getTotalElements())
                .items(statsList)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public TotalWordsLearnedResponse getTotalWordsLearning(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Long totalLearningWords = learnedWordRepository.countByUserId(userId);
        if (totalLearningWords == null) {
            totalLearningWords = 0L;
        }

        return TotalWordsLearnedResponse.builder()
                .userId(userId)
                .type("LEARNING")
                .totalWords(totalLearningWords)
                .period("ALL TIME")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public TotalWordsLearnedResponse getTotalWordsLearned(String userId, LocalDate date, String type) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chuyển LocalDate sang java.util.Date
        Date inputDate = Date.from(date.atStartOfDay(ZoneId.of("UTC")).toInstant());
        log.info("Input: userId={}, date={}, type={}", userId, inputDate, type);

        Long totalWords = 0L;
        String period;

        switch (type.toUpperCase()) {
            case "DAY":
                Long dayCount = learnedWordRepository.countByUserIdAndDay(userId, inputDate);
                totalWords = dayCount != null ? dayCount : 0L;
                period = date.format(DateTimeFormatter.ISO_LOCAL_DATE); // Ví dụ: "2025-05-05"
                log.info("DAY count: {}", totalWords);
                break;

            case "WEEK":
                Long weekCount = learnedWordRepository.countByUserIdAndWeek(userId, inputDate);
                totalWords = weekCount != null ? weekCount : 0L;
                int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                period = date.getYear() + " Week " + weekNumber; // Ví dụ: "2025 Week 19"
                log.info("WEEK count: {}", totalWords);
                break;

            case "MONTH":
                Long monthCount = learnedWordRepository.countByUserIdAndMonth(userId, inputDate);
                totalWords = monthCount != null ? monthCount : 0L;
                period = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)); // Ví dụ: "May 2025"
                log.info("MONTH count: {}", totalWords);
                break;

            case "YEAR":
                Long yearCount = learnedWordRepository.countByUserIdAndYear(userId, inputDate);
                totalWords = yearCount != null ? yearCount : 0L;
                period = String.valueOf(date.getYear()); // Ví dụ: "2025"
                log.info("YEAR count: {}", totalWords);
                break;

            default:
                throw new IllegalArgumentException("Invalid type: " + type + ". Must be DAY, WEEK, MONTH, or YEAR.");
        }

        log.info("Output: totalWords={}, period={}", totalWords, period);

        return TotalWordsLearnedResponse.builder()
                .userId(userId)
                .type(type.toUpperCase())
                .totalWords(totalWords)
                .period(period)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public WordsLearnedStatsResponse getWordLearningStats(String userId, LocalDate date, String type) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chuyển LocalDate sang java.util.Date
        Date inputDate = Date.from(date.atStartOfDay(ZoneId.of("UTC")).toInstant());
        log.info("Input: userId={}, date={}, type={}", userId, inputDate, type);

        List<WordsLearnedStatsResponse.StatDetail> details = new ArrayList<>();
        String period;

        switch (type.toUpperCase()) {
            case "DAY_IN_WEEK":
                List<Object[]> dayResults = learnedWordRepository.countWordsByDayInWeek(userId, inputDate);
                for (Object[] result : dayResults) {
                    WordsLearnedStatsResponse.StatDetail detail = new WordsLearnedStatsResponse.StatDetail();
                    detail.setTimeUnit((String) result[0]); // dayname
                    detail.setWordCount(((Number) result[1]).longValue()); // word_count
                    details.add(detail);
                }
                int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                period = date.getYear() + " Week " + weekNumber; // Ví dụ: "2025 Week 19"
                break;

            case "WEEK_IN_MONTH":
                List<Object[]> weekResults = learnedWordRepository.countWordsByWeekInMonth(userId, inputDate);
                for (Object[] result : weekResults) {
                    WordsLearnedStatsResponse.StatDetail detail = new WordsLearnedStatsResponse.StatDetail();
                    detail.setTimeUnit(String.valueOf(((Number) result[0]).intValue())); // week_in_month
                    detail.setWordCount(((Number) result[1]).longValue()); // word_count
                    details.add(detail);
                }
                period = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)); // Ví dụ: "May 2025"
                break;

            case "MONTH_IN_YEAR":
                List<Object[]> monthResults = learnedWordRepository.countWordsByMonthInYear(userId, inputDate);
                for (Object[] result : monthResults) {
                    WordsLearnedStatsResponse.StatDetail detail = new WordsLearnedStatsResponse.StatDetail();
                    detail.setTimeUnit(String.valueOf(((Number) result[0]).intValue())); // month
                    detail.setWordCount(((Number) result[1]).longValue()); // word_count
                    details.add(detail);
                }
                period = String.valueOf(date.getYear()); // Ví dụ: "2025"
                break;

            default:
                throw new IllegalArgumentException("Invalid type: " + type + ". Must be DAY_IN_WEEK, WEEK_IN_MONTH, or MONTH_IN_YEAR.");
        }

        log.info("Output: type={}, period={}, details={}", type, period, details);
        return WordsLearnedStatsResponse.builder()
                .userId(userId)
                .type(type.toUpperCase())
                .details(details)
                .period(period)
                .build();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}

