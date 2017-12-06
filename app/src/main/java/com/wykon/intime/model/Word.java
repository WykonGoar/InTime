package com.wykon.intime.model;

/**
 * Created by 52 on 27-11-2017.
 */

public class Word {

    private int mId;
    private String mWord;
    private int mUsedLocation;

    public Word(){}

    public Word(int id, String word, int usedLocation)
    {
        mId = id;
        mWord = word;
        mUsedLocation = usedLocation;
    }

    public int getId(){
        return mId;
    }

    public String getWord() {
        return mWord;
    }

    public int getUsedLocation() {
        return mUsedLocation;
    }

    public void setWord(String word) {
        this.mWord = word;
    }

    public void setUsedLocation(int usedLocation) {
        this.mUsedLocation = usedLocation;
    }
}
