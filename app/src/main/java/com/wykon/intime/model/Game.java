package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by 52 on 29-11-2017.
 */

public class Game implements Serializable{
    private int mId;
    private int mWordCount;
    private int mWinPoints;
    private LinkedList<Player> mPlayerOrder = new LinkedList<Player>();
    private LinkedList<Score> mScores = new LinkedList<Score>();

    public Game(int wordCount, int winPoints){
        mWordCount = wordCount;
        mWinPoints = winPoints;
    }

    public int getId() {
        return mId;
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

    public void insert(DatabaseConnection databaseConnection){
        int firstPlayer = 0;
        String sPlayerOrder = "";
        String sScoreList = "";

        for (int index = 0; index < mPlayerOrder.size(); index++){
            Player player = mPlayerOrder.get(index);
            if (index == 0)
                firstPlayer = player.getId();

            sPlayerOrder += player.getId() + ";";
            sScoreList += "0;";
        }

        String query = "INSERT INTO games(win_points, word_count, player_order, score_list, last_player, latest) VALUES(?,?,?,?,?,1)";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mWinPoints);
        statement.bindLong(2, mWordCount);
        statement.bindString(3, sPlayerOrder);
        statement.bindString(4, sScoreList);
        statement.bindLong(5, firstPlayer);

        mId = databaseConnection.executeInsertQuery(statement);
    }

    public void generateTeamOrder(DatabaseConnection databaseConnection){
        LinkedList<Team> teams = databaseConnection.getTeams();
        Random random = new Random();

        List<Integer> usedIndex = new LinkedList<Integer>();
        LinkedList<Team> teamOrder = new LinkedList<Team>();

        for (int i = 0; i < teams.size(); i++){
            int index;
            do {
                index = random.nextInt(teams.size() - 1);
            } while (!usedIndex.contains(index));

            teamOrder.add(teams.get(index));
            usedIndex.add(index);
        }

        Map<Integer, LinkedList<Integer>> usedIndexes = new HashMap<Integer, LinkedList<Integer>>();
        //Loop teams
        //Get player
        // ends when length team index size is players size
        // int teamsDone == teams size
    }
}
