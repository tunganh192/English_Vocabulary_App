package com.honda.englishapp.english_learning_backend.mapper;

import com.honda.englishapp.english_learning_backend.dto.request.LearnedWord.LearnedWordUpdateRequest;
import com.honda.englishapp.english_learning_backend.dto.response.LearnedWordResponse;
import com.honda.englishapp.english_learning_backend.entity.LearnedWord;
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
public class LearnedWordMapperImpl implements LearnedWordMapper {

    @Override
    public LearnedWordResponse toLearnedWordResponse(LearnedWord learnedWord) {
        if ( learnedWord == null ) {
            return null;
        }

        LearnedWordResponse.LearnedWordResponseBuilder learnedWordResponse = LearnedWordResponse.builder();

        learnedWordResponse.userId( learnedWordUserId( learnedWord ) );
        learnedWordResponse.wordId( learnedWordWordId( learnedWord ) );
        learnedWordResponse.dateLearned( dateToLocalDateTime( learnedWord.getDateLearned() ) );
        learnedWordResponse.id( learnedWord.getId() );
        learnedWordResponse.correctCount( learnedWord.getCorrectCount() );
        learnedWordResponse.wrongCount( learnedWord.getWrongCount() );
        learnedWordResponse.correctStreak( learnedWord.getCorrectStreak() );

        return learnedWordResponse.build();
    }

    @Override
    public void updateLearnedWord(LearnedWord learnedWord, LearnedWordUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        learnedWord.setCorrectCount( request.getCorrectCount() );
        learnedWord.setWrongCount( request.getWrongCount() );
        learnedWord.setCorrectStreak( request.getCorrectStreak() );
        learnedWord.setMastered( request.isMastered() );
    }

    @Override
    public void patchLearnedWord(LearnedWord learnedWord, LearnedWordUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        learnedWord.setCorrectCount( request.getCorrectCount() );
        learnedWord.setWrongCount( request.getWrongCount() );
        learnedWord.setCorrectStreak( request.getCorrectStreak() );
        learnedWord.setMastered( request.isMastered() );
    }

    private String learnedWordUserId(LearnedWord learnedWord) {
        if ( learnedWord == null ) {
            return null;
        }
        User user = learnedWord.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long learnedWordWordId(LearnedWord learnedWord) {
        if ( learnedWord == null ) {
            return null;
        }
        Word word = learnedWord.getWord();
        if ( word == null ) {
            return null;
        }
        Long id = word.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
