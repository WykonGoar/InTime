package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;

/**
 * Created by 52 on 27-11-2017.
 */

public class Word implements Serializable{
    private int mId = -1;
    private String mWord;
    private boolean mSelected = true;

    public Word(String word){
        mWord = word;
    }

    public Word(int id, String word, boolean selected)
    {
        mId = id;
        mWord = word;
        mSelected = selected;
    }

    public int getId(){
        return mId;
    }

    public String getWord() {
        return mWord;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public void save(DatabaseConnection databaseConnection){
        String query = "UPDATE words SET word = ?, selected = ? WHERE _id = ?";

        int iSelected = 0;
        if (mSelected)
            iSelected = 1;

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mWord);
        statement.bindLong(2, iSelected);
        statement.bindLong(3, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public void insert(DatabaseConnection databaseConnection, int list_id){
        String query = "INSERT INTO words(word, selected, list_id) VALUES(?, ?, ?);";

        int iSelected = 0;
        if (mSelected)
            iSelected = 1;

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mWord);
        statement.bindLong(2, iSelected);
        statement.bindLong(3, list_id);

        mId = databaseConnection.executeInsertQuery(statement);
    }

    public void delete(DatabaseConnection databaseConnection) {
        String query = "DELETE FROM words WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }
}
