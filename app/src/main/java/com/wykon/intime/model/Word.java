package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;

/**
 * Created by 52 on 27-11-2017.
 */

public class Word implements Serializable{
    private int mId = -1;
    private String mWord;
    private int mUsedLocation;

    public Word(String word){
        mWord = word;
    }

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
        mWord = word;
    }

    public void setUsedLocation(int usedLocation) {
        mUsedLocation = usedLocation;
    }

    public void save(DatabaseConnection databaseConnection){
        String query = "UPDATE groups SET name = ? WHERE _id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mWord);
        statement.bindLong(2, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public void insert(DatabaseConnection databaseConnection, int list_id){
        String query = "INSERT INTO words(name, list_id) VALUES(?);";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mWord);
        statement.bindLong(2, list_id);

        databaseConnection.executeNonReturn(statement);
    }

    public void delete(DatabaseConnection databaseConnection) {
        String query = "DELETE FROM words WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }
}
