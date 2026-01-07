package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Word.WordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Word.WordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.WordResponse;
import com.honda.englishapp.english_learning_backend.entity.Word;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WordMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    Word toWord(WordCreationRequest request);

    @Mapping(source = "createdBy.id", target = "createdBy")
    @Mapping(source = "category.id", target = "categoryId")
    WordResponse toWordResponse(Word word);

    @Mapping(target = "id", ignore = true)
    void updateWord(@MappingTarget Word word, WordUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)// để MapStruct tự bỏ qua các trường null.
    void patchWord(@MappingTarget Word word, WordUpdateRequest request);
}
