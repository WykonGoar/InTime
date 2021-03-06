package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;

/**
 * Created by 52 on 29-11-2017.
 */

public class Settings implements Serializable{
    private int mWordCount;
    private int mWinPoints;

    public Settings(int wordCount, int winPoints){
        mWordCount = wordCount;
        mWinPoints = winPoints;
    }

    public int getWordCount() {
        return mWordCount;
    }

    public int getWinPoints() {
        return mWinPoints;
    }

    public void setWordCount(int wordCount) {
        mWordCount = wordCount;
    }

    public void setWinPoints(int winPoints) {
        mWinPoints = winPoints;
    }

    public void save(DatabaseConnection databaseConnection){
        String query = "UPDATE settings SET win_points = ?, word_count = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mWinPoints);
        statement.bindLong(2
                , mWordCount);

        databaseConnection.executeNonReturn(statement);
    }
}
