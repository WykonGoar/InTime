package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;

/**
 * Created by 52 on 06-12-2017.
 */

public class Score implements Serializable {
    private Team mTeam;
    private int mScore;

    public Score(Team team){
        mTeam = team;
    }

    public Score(Team team, int score){
        mTeam = team;
        mScore = score;
    }

    public Team getGroup() {
        return mTeam;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public void save(DatabaseConnection databaseConnection){
        String query = "UPDATE scores SET score = ? WHERE group_id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mScore);
        statement.bindLong(2, mTeam.getId());

        databaseConnection.executeNonReturn(statement);
    }

    private void insert(DatabaseConnection databaseConnection){
        String query = "INSERT INTO scores(group_id) VALUES(?);";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mTeam.getId());

        databaseConnection.executeNonReturn(statement);
    }
}
