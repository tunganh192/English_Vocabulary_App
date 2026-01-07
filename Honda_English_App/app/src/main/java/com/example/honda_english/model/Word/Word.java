package com.example.honda_english.model.Word;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    private String english;
    private String vietnamese;
    private long id;
    private String pronunciation;

    public Word(String english, String vietnamese, long id, String pronunciation) {
        this.english = english;
        this.vietnamese = vietnamese;
        this.id = id;
        this.pronunciation = pronunciation;
    }

    protected Word(Parcel in) {
        english = in.readString();
        vietnamese = in.readString();
        id = in.readLong();
        pronunciation = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(english);
        dest.writeString(vietnamese);
        dest.writeLong(id);
        dest.writeString(pronunciation);
    }

    // Getters
    public String getEnglish() {
        return english;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    public Long getId() {
        return id;
    }
    public String getPronunciation() {
        return pronunciation;
    }
}