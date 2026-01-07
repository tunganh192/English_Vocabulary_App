package com.honda.englishapp.english_learning_backend.service;

import com.honda.englishapp.english_learning_backend.dto.request.Question.CheckAnswerRequest;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.CheckAnswerResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.MultipleChoiceQuestionResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.TrueFalseQuestionResponse;
import com.honda.englishapp.english_learning_backend.dto.response.Questtions.WordAssemblyQuestionResponse;
import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.Word;
import com.honda.englishapp.english_learning_backend.exception.AppException;
import com.honda.englishapp.english_learning_backend.exception.ErrorCode;
import com.honda.englishapp.english_learning_backend.repository.CategoryRepository;
import com.honda.englishapp.english_learning_backend.repository.LearnedWordRepository;
import com.honda.englishapp.english_learning_backend.repository.UserRepository;
import com.honda.englishapp.english_learning_backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class QuestionService {
    WordRepository wordRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    LearnedWordRepository learnedWordRepository;
    Random random = new Random();

    private final Map<String, Map<String, Object>> questionAnswers = new HashMap<>();

    public TrueFalseQuestionResponse generateTrueFalseQuestion(Long categoryId, String userId, boolean fromMasteredWords) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        List<Long> masteredWordIds = learnedWordRepository.findMasteredWordIdsByUserId(userId);

        if (fromMasteredWords && masteredWordIds.isEmpty()) {
            throw new AppException(ErrorCode.NO_MASTERED_WORDS);
        }

        Optional<Word> wordOpt = fromMasteredWords
                ? wordRepository.findRandomByIsActiveTrueAndCategoryIdAndInWordIds(categoryId, masteredWordIds)
                : wordRepository.findRandomByIsActiveTrueAndCategoryIdAndNotMastered(categoryId, masteredWordIds);

        Word word = wordOpt.orElseGet(() -> {
            return wordRepository.findRandomByIsActiveTrueAndCategoryId(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_WORDS));
        });

        boolean isCorrect = random.nextBoolean();
        String meaning;
        if (isCorrect) {
            meaning = word.getVietnameseMeaning();
        } else {
            Optional<String> wrongMeaningOpt = wordRepository.findRandomVietnameseMeaningByCategoryIdAndNotWordId(categoryId, word.getId());
            if (wrongMeaningOpt.isEmpty()) {
                List<String> extraGlobal = wordRepository.findRandomVietnameseMeaningsAndNotWordId(word.getId(), PageRequest.of(0, 1));
                wrongMeaningOpt = Optional.ofNullable(extraGlobal.getFirst());
            }
            meaning = wrongMeaningOpt.orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_WRONG_MEANINGS));
            isCorrect = meaning.equals(word.getVietnameseMeaning());
        }

        // Lưu đáp án
        String key = userId + "_" + word.getId();
        System.out.println("Generating TrueFalseQuestion - Key: " + key); // Log key
        Map<String, Object> answerData = new HashMap<>();
        answerData.put("correctMeaning", word.getVietnameseMeaning());
        //answerData.put("isCorrect", isCorrect);
        questionAnswers.put(key, answerData);

        return TrueFalseQuestionResponse.builder()
                .wordId(word.getId())
                .word(word.getEnglishWord())
                .displayedMeaning(meaning)
                .build();
    }

    public MultipleChoiceQuestionResponse generateMultipleChoiceQuestionWith4Options(Long categoryId, String userId, boolean fromMasteredWords) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        List<Long> masteredWordIds = learnedWordRepository.findMasteredWordIdsByUserId(userId);

        if (fromMasteredWords && masteredWordIds.isEmpty()) {
            throw new AppException(ErrorCode.NO_MASTERED_WORDS);
        }

        Optional<Word> wordOpt = fromMasteredWords
                ? wordRepository.findRandomByIsActiveTrueAndCategoryIdAndInWordIds(categoryId, masteredWordIds)
                : wordRepository.findRandomByIsActiveTrueAndCategoryIdAndNotMastered(categoryId, masteredWordIds);

        Word word = wordOpt.orElseGet(() -> {
            return wordRepository.findRandomByIsActiveTrueAndCategoryId(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_WORDS));
        });

        List<String> options = new ArrayList<>();
        String correctMeaning = word.getVietnameseMeaning();
        options.add(correctMeaning);

        List<String> wrongMeanings = wordRepository.findThreeRandomVietnameseMeaningsByCategoryIdAndNotWordId(categoryId, word.getId());

        int needed = 3 - wrongMeanings.size();
        if (needed > 0) {
            List<String> extraGlobal = wordRepository.findRandomVietnameseMeaningsAndNotWordId(word.getId(), PageRequest.of(0, needed));
            wrongMeanings.addAll(extraGlobal);
        }

        options.addAll(wrongMeanings);
        Collections.shuffle(options, random);
        int correctAnswerIndex = options.indexOf(correctMeaning);

        // Lưu đáp án
        String key = userId + "_" + word.getId();
        System.out.println("Generating MultipleChoiceQuestion - Key: " + key); // Log key
        Map<String, Object> answerData = new HashMap<>();
        //answerData.put("correctMeaning", correctMeaning);
        answerData.put("correctAnswerIndex", correctAnswerIndex);
        questionAnswers.put(key, answerData);

        return MultipleChoiceQuestionResponse.builder()
                .wordId(word.getId())
                .word(word.getEnglishWord())
                .options(options)
                .build();
    }

    public CheckAnswerResponse checkAnswer(CheckAnswerRequest request) {
        String key = request.getUserId() + "_" + request.getWordId();
        Map<String, Object> answerData = questionAnswers.get(key);
        if (answerData == null) {
            System.out.println("Question not found for key: " + key);
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
        }

        boolean isCorrect;
        String correctAnswer;
        String userAnswer = request.getAnswer();

        switch (request.getQuestionType()) {
            case "TRUE_FALSE":
                isCorrect = userAnswer.equalsIgnoreCase(String.valueOf(answerData.get("correctMeaning")));
                correctAnswer = String.valueOf(answerData.get("correctMeaning"));
                break;
            case "MULTIPLE_CHOICE":
                int correctAnswerIndex = (int) answerData.get("correctAnswerIndex");
                isCorrect = userAnswer.equals(String.valueOf(correctAnswerIndex));
                correctAnswer = String.valueOf(correctAnswerIndex);
                break;
            case "WORD_ASSEMBLY":
                String correctWord = (String) answerData.get("correctWord");
                isCorrect = userAnswer.equalsIgnoreCase(correctWord);
                correctAnswer = correctWord;
                break;
            default:
                throw new AppException(ErrorCode.INVALID_QUESTION_TYPE);
        }

        // Cập nhật learned_word
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_EXISTED));

        LearnedWord learnedWord = learnedWordRepository.findByUserIdAndWordId(request.getUserId(), request.getWordId())
                .orElse(LearnedWord.builder()
                        .user(user)
                        .word(word)
                        .correctCount(0)
                        .wrongCount(0)
                        .correctStreak(0)
                        .isMastered(false)
                        .build());

        if (isCorrect) {
            learnedWord.setCorrectCount(learnedWord.getCorrectCount() + 1);
            learnedWord.setCorrectStreak(learnedWord.getCorrectStreak() + 1);
            if (learnedWord.getCorrectStreak() == 4) {
                learnedWord.setMastered(true);
            }
        } else {
            learnedWord.setWrongCount(learnedWord.getWrongCount() + 1);
            learnedWord.setCorrectStreak(0);
        }

        learnedWordRepository.save(learnedWord);

        // Xóa đáp án sau khi kiểm tra
        questionAnswers.remove(key);

        return isCorrect
                ? CheckAnswerResponse.builder().isCorrect(true).build()
                : CheckAnswerResponse.builder()
                .isCorrect(false)
                .correctAnswer(correctAnswer)
                .userAnswer(userAnswer)
                .build();
    }

    public MultipleChoiceQuestionResponse generateMultipleChoiceQuestionWith6Options(Long categoryId, String userId, boolean fromMasteredWords) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        List<Long> masteredWordIds = learnedWordRepository.findMasteredWordIdsByUserId(userId);

        if (fromMasteredWords && masteredWordIds.isEmpty()) {
            throw new AppException(ErrorCode.NO_MASTERED_WORDS);
        }

        Optional<Word> wordOpt = fromMasteredWords
                ? wordRepository.findRandomByIsActiveTrueAndCategoryIdAndInWordIds(categoryId, masteredWordIds)
                : wordRepository.findRandomByIsActiveTrueAndCategoryIdAndNotMastered(categoryId, masteredWordIds);

        Word word = wordOpt.orElseGet(() -> {
            return wordRepository.findRandomByIsActiveTrueAndCategoryId(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_WORDS));
        });

        List<String> options = new ArrayList<>();
        String correctMeaning = word.getVietnameseMeaning();
        options.add(correctMeaning);

        List<String> wrongMeanings = wordRepository.findFiveRandomVietnameseMeaningsByCategoryIdAndNotWordIds(categoryId, word.getId());

        int needed = 5 - wrongMeanings.size();
        if (needed > 0) {
            List<String> extraGlobal = wordRepository.findRandomVietnameseMeaningsAndNotWordId(word.getId(), PageRequest.of(0, needed));
            wrongMeanings.addAll(extraGlobal);
        }

        options.addAll(wrongMeanings);

        Collections.shuffle(options, random);
        int correctAnswerIndex = options.indexOf(correctMeaning);

        // Lưu đáp án
        String key = userId + "_" + word.getId();
        Map<String, Object> answerData = new HashMap<>();
        //answerData.put("correctMeaning", correctMeaning);
        answerData.put("correctAnswerIndex", correctAnswerIndex);
        questionAnswers.put(key, answerData);

        return MultipleChoiceQuestionResponse.builder()
                .wordId(word.getId())
                .word(word.getEnglishWord())
                .options(options)
                .build();
    }

    public WordAssemblyQuestionResponse generateWordAssemblyWithOptions(Long categoryId, String userId, boolean fromMasteredWords) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        // Lấy danh sách word_id đã mastered
        List<Long> masteredWordIds = learnedWordRepository.findMasteredWordIdsByUserId(userId);

        if (fromMasteredWords && masteredWordIds.isEmpty()) {
            throw new AppException(ErrorCode.NO_MASTERED_WORDS);
        }

        Optional<Word> wordOpt = fromMasteredWords
                ? wordRepository.findRandomByIsActiveTrueAndCategoryIdAndInWordIds(categoryId, masteredWordIds)
                : wordRepository.findRandomByIsActiveTrueAndCategoryIdAndNotMastered(categoryId, masteredWordIds);

        Word word = wordOpt.orElseGet(() -> {
            return wordRepository.findRandomByIsActiveTrueAndCategoryId(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_WORDS));
        });

        String englishWord = word.getEnglishWord();
        if (englishWord.length() < 2) {
            throw new AppException(ErrorCode.WORD_TOO_SHORT);
        }

        // Chia englishWord
        List<String> wordParts = splitWord(englishWord);
        List<String> parts = new ArrayList<>(wordParts);

        // Lấy 3 distractor parts
        List<String> distractorParts = generateDistractorParts(categoryId, word.getId(), masteredWordIds);
        parts.addAll(distractorParts);

        // Xáo trộn parts
        Collections.shuffle(parts, random);

        // Lưu đáp án
        String key = userId + "_" + word.getId();
        Map<String, Object> answerData = new HashMap<>();
        answerData.put("correctWord", englishWord);
        questionAnswers.put(key, answerData);

        return WordAssemblyQuestionResponse.builder()
                .wordId(word.getId())
                .meaning(word.getVietnameseMeaning())
                .parts(parts)
                .build();
    }

    private List<String> splitWord(String word) {
        int length = word.length();
        List<String> parts = new ArrayList<>();

        if (length < 3) {
            // Chia mỗi ký tự thành 1 phần
            for (int i = 0; i < length; i++) {
                parts.add(word.substring(i, i + 1));
            }
            return parts;
        }

        // Chọn ngẫu nhiên 2 hoặc 3 phần
        int numParts = random.nextInt(2) + 2; // 2 or 3
        List<Integer> splitPoints = new ArrayList<>();

        // Chọn numParts-1 điểm chia ngẫu nhiên
        for (int i = 0; i < numParts - 1; i++) {
            int maxSplit = length - (numParts - 1 - i); // Đảm bảo mỗi phần sau có ít nhất 1 ký tự
            int minSplit = i == 0 ? 1 : splitPoints.get(i - 1) + 1; // Đảm bảo điểm chia tăng dần
            int splitPoint = random.nextInt(maxSplit - minSplit + 1) + minSplit;
            splitPoints.add(splitPoint);
        }

        // Thêm điểm đầu và cuối
        splitPoints.add(0, 0);
        splitPoints.add(length);

        // Chia từ theo split points
        for (int i = 0; i < numParts; i++) {
            parts.add(word.substring(splitPoints.get(i), splitPoints.get(i + 1)));
        }

        return parts;
    }

    private List<String> generateDistractorParts(Long categoryId, Long excludeWordId, List<Long> masteredWordIds) {
        List<String> distractorParts = new ArrayList<>();
        Set<String> usedWords = new HashSet<>(); // Tránh lặp từ

        while (distractorParts.size() < 3) {
            String distractorWord = wordRepository.findRandomEnglishWordByCategoryIdAndNotWordIdAndNotMastered(categoryId, excludeWordId, masteredWordIds)
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_DISTRACTORS));

            // Tránh dùng lại cùng distractorWord
            if (usedWords.contains(distractorWord)) {
                continue;
            }
            usedWords.add(distractorWord);

            // Chia distractorWord
            List<String> parts = splitWord(distractorWord);

            // Loại bỏ nếu part là từ hoàn chỉnh
            parts.removeIf(part -> part.equals(distractorWord));

            // Thêm parts vào distractorParts
            distractorParts.addAll(parts);

            // Nếu > 3 parts, loại ngẫu nhiên
            while (distractorParts.size() > 3) {
                distractorParts.remove(random.nextInt(distractorParts.size()));
            }

            // Nếu < 3 parts, bổ sung bằng cách lặp lại hoặc kết hợp
            if (distractorParts.size() < 3 && !parts.isEmpty()) {
                // Lặp lại một phần ngẫu nhiên
                distractorParts.add(parts.get(random.nextInt(parts.size())));
                if (distractorParts.size() < 3 && parts.size() >= 2) {
                    // Kết hợp hai phần
                    String combined = parts.get(0) + parts.get(1);
                    if (!combined.equals(distractorWord)) { // Không thêm từ hoàn chỉnh
                        distractorParts.add(combined);
                    }
                }
            }
        }

        // Đảm bảo đúng 3 parts
        while (distractorParts.size() > 3) {
            distractorParts.remove(random.nextInt(distractorParts.size()));
        }

        return distractorParts;
    }

}