package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.LearnedWordResponse;
import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface LearnedWordMapper {

    //LearnedWord toLearnedWord(LearnedWordCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "word.id", target = "wordId")
    @Mapping(source = "dateLearned", target = "dateLearned", qualifiedByName = "dateToLocalDateTime")
    LearnedWordResponse toLearnedWordResponse(LearnedWord learnedWord);

    @Named("dateToLocalDateTime")
    default LocalDateTime dateToLocalDateTime(Date date) {
        return date != null ? LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Asia/Ho_Chi_Minh")) : null;    }

    @Mapping(target = "id", ignore = true)
    void updateLearnedWord(@MappingTarget LearnedWord learnedWord, LearnedWordUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)// để MapStruct tự bỏ qua các trường null.
    void patchLearnedWord(@MappingTarget LearnedWord learnedWord, LearnedWordUpdateRequest request);

}
