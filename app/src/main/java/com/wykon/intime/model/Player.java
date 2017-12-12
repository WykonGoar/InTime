package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;

/**
 * Created by 52 on 06-12-2017.
 */

public class Player implements Serializable{
    private int mId = -1;
    private String mName;

    public Player(String name){
        mName = name;
    }

    public Player(int id, String name){
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = mName;
    }

    public void save(DatabaseConnection databaseConnection, int group_id){
        String query = "UPDATE players SET name = ? WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mName);
        statement.bindLong(2, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public void insert(DatabaseConnection databaseConnection, int group_id){
        String query = "INSERT INTO players(name, team_id) VALUES(?, ?);";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mName);
        statement.bindLong(2, group_id);

        mId = databaseConnection.executeInsertQuery(statement);
    }

    public void delete(DatabaseConnection databaseConnection){
        String query = "DELETE FROM players WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }
}
