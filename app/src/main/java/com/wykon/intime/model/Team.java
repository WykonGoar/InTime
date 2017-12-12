package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 52 on 06-12-2017.
 */

public class Team implements Serializable{
    private int mId = -1;
    private String mName;
    private List<Player> mPlayers = new LinkedList<Player>();

    public Team(){}

    public Team(int id, String name, List<Player> players){
        mId = id;
        mName = name;
        mPlayers = players;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPlayers(List<Player> players){
        mPlayers = players;
    }

    public void save(DatabaseConnection databaseConnection){
        if(mId == -1) {
            insert(databaseConnection);
            return;
        }

        update(databaseConnection);

    }

    private void insert(DatabaseConnection databaseConnection){
        String query = "INSERT INTO teams(name) VALUES(?);";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mName);

        mId = databaseConnection.executeInsertQuery(statement);

        addPlayers(databaseConnection);
    }

    private void update(DatabaseConnection databaseConnection){
        String query = "UPDATE teams SET name = ? WHERE _id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindString(1, mName);
        statement.bindLong(2, mId);

        databaseConnection.executeNonReturn(statement);

        clearPlayers(databaseConnection);
        addPlayers(databaseConnection);
    }

    public void delete(DatabaseConnection databaseConnection){
        String query = "DELETE FROM teams WHERE _id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }

    private void clearPlayers(DatabaseConnection databaseConnection){
        String query = "DELETE FROM players WHERE team_id = ?;";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }

    private void addPlayers(DatabaseConnection databaseConnection){
        for(Player player : mPlayers){
            player.insert(databaseConnection, mId);
        }
    }
}
