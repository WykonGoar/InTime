package com.wykon.intime.model;

import java.util.LinkedList;

/**
 * Created by 52 on 27-11-2017.
 */

public class WordList {
    private int mId;
    private String mName;
    private boolean mSelected = false;
    private LinkedList<Word> mWords;

    public WordList(){}

    public WordList(int id, String name, boolean selected, LinkedList<Word> words){
        mId = id;
        mName = name;
        mSelected = selected;
        mWords = words;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public LinkedList<Word> getWords(){
        return mWords;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
