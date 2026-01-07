package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.Word.WordCreationRequest;
import com.honda.englishapp.english_learning_backend.dto.request.Word.WordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.WordResponse;
import com.honda.englishapp.english_learning_backend.entity.Category;
import com.honda.englishapp.english_learning_backend.entity.User;
import com.honda.englishapp.english_learning_backend.entity.Word;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-27T10:27:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class WordMapperImpl implements WordMapper {

    @Override
    public Word toWord(WordCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Word.WordBuilder word = Word.builder();

        word.englishWord( request.getEnglishWord() );
        word.pronunciation( request.getPronunciation() );
        word.vietnameseMeaning( request.getVietnameseMeaning() );

        return word.build();
    }

    @Override
    public WordResponse toWordResponse(Word word) {
        if ( word == null ) {
            return null;
        }

        WordResponse.WordResponseBuilder wordResponse = WordResponse.builder();

        wordResponse.createdBy( wordCreatedById( word ) );
        wordResponse.categoryId( wordCategoryId( word ) );
        wordResponse.id( word.getId() );
        wordResponse.englishWord( word.getEnglishWord() );
        wordResponse.vietnameseMeaning( word.getVietnameseMeaning() );
        wordResponse.pronunciation( word.getPronunciation() );

        return wordResponse.build();
    }

    @Override
    public void updateWord(Word word, WordUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        word.setEnglishWord( request.getEnglishWord() );
        word.setPronunciation( request.getPronunciation() );
        word.setVietnameseMeaning( request.getVietnameseMeaning() );
        word.setIsActive( request.getIsActive() );
    }

    @Override
    public void patchWord(Word word, WordUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getEnglishWord() != null ) {
            word.setEnglishWord( request.getEnglishWord() );
        }
        if ( request.getPronunciation() != null ) {
            word.setPronunciation( request.getPronunciation() );
        }
        if ( request.getVietnameseMeaning() != null ) {
            word.setVietnameseMeaning( request.getVietnameseMeaning() );
        }
        if ( request.getIsActive() != null ) {
            word.setIsActive( request.getIsActive() );
        }
    }

    private String wordCreatedById(Word word) {
        if ( word == null ) {
            return null;
        }
        User createdBy = word.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String id = createdBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long wordCategoryId(Word word) {
        if ( word == null ) {
            return null;
        }
        Category category = word.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
