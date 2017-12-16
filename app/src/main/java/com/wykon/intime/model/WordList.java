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
    private LinkedList<Word> mWords = new LinkedList<Word>();

    public WordList() {}

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

    public void setName(String name) {
        mName = name;
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
        statement.bindString(1, mName);

        mId = databaseConnection.executeInsertQuery(statement);

        addWords(databaseConnection);
    }

    private void update(DatabaseConnection databaseConnection){
        String query = "UPDATE lists SET name = ?, selected = ? WHERE _id = ?";

        int iSelected = 0;
        if (mSelected)
            iSelected = 1;

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mName);
        statement.bindLong(2, iSelected);
        statement.bindLong(3, mId);

        databaseConnection.executeNonReturn(statement);

        clearWords(databaseConnection);
        addWords(databaseConnection);
    }

    public void delete(DatabaseConnection databaseConnection){
        String query = "DELETE FROM lists WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);

        clearWords(databaseConnection);
        addWords(databaseConnection);
    }

    private void clearWords(DatabaseConnection databaseConnection){
        String query = "DELETE FROM words WHERE list_id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }

    private void addWords(DatabaseConnection databaseConnection){
        for(Word mWord : mWords){
            mWord.insert(databaseConnection, mId);
        }
    }
}
