package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by 52 on 27-11-2017.
 */

public class WordList implements Serializable{
    private int mId = -1;
    private String mName;
    private boolean mSelected = false;
    private LinkedList<Word> mWords;

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

    public boolean isSelected() {
        return mSelected;
    }

    public LinkedList<Word> getWords(){
        return mWords;
    }

    public void setName(String mName) {
        mName = mName;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public void save(DatabaseConnection databaseConnection){
        if(mId == -1) {
            insert(databaseConnection);
            return;
        }

        update(databaseConnection);

    }

    private void insert(DatabaseConnection databaseConnection){
        String query = "INSERT INTO lists(name) VALUES(?);";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(0, mName);

        databaseConnection.executeNonReturn(statement);
    }

    private void update(DatabaseConnection databaseConnection){
        String query = "UPDATE lists SET name = ? WHERE _id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(0, mName);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public void delete(DatabaseConnection databaseConnection){
        String query = "DELETE FROM lists WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(0, mId);

        databaseConnection.executeNonReturn(statement);
    }

    private void addWord(DatabaseConnection databaseConnection, String word){
        Word newWord = new Word(word);

        newWord.insert(databaseConnection, mId);
    }
}
