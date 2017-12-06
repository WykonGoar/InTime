package com.wykon.intime.model;

/**
 * Created by 52 on 29-11-2017.
 */

public class Game {
    private int mWordCount;
    private int mWinPoints;
    private int mScore;
    private String mMode;

    public Game(){}

    public Game(int wordCount, int winPoints, int score, String mode){
        mWordCount = wordCount;
        mWinPoints = winPoints;
        mScore = score;
        mMode = mode;
    }

    public int getWordCount() {
        return mWordCount;
    }

    public void setWordCount(int wordCount) {
        this.mWordCount = wordCount;
    }

    public int getWinPoints() {
        return mWinPoints;
    }

    public void setWinPoints(int winPoints) {
        this.mWinPoints = winPoints;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mode) {
        this.mMode = mode;
    }
}
