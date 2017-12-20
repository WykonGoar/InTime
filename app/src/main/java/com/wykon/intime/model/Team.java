package com.wykon.intime.model;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by 52 on 06-12-2017.
 */

public class Team implements Serializable, Comparable<Team>{
    private int mId = -1;
    private String mName;
    private List<Player> mPlayers = new LinkedList<Player>();
    private List<Player> mPlayerOrder = new LinkedList<Player>();
    private int mScore = 0;
    private int mLastPlayerIndex = -1;

    public Team(){}

    public Team(int id, String name, int score, int lastPlayerIndex, List<Player> players, List<Player> playerOrder){
        mId = id;
        mName = name;
        mScore = score;
        mLastPlayerIndex = lastPlayerIndex;
        mPlayers = players;
        mPlayerOrder = playerOrder;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<Player> getPlayerOrder() {
        return mPlayerOrder;
    }

    public int getScore() {
        return mScore;
    }

    public void resetScore(DatabaseConnection databaseConnection){
        mScore = 0;

        String query = "UPDATE teams SET score = 0 WHERE _id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public int getLastPlayerIndex() {
        return mLastPlayerIndex;
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

    public void setPlayerOrder(List<Player> playerOrder) {
        mPlayerOrder = playerOrder;
    }

    public void setLastPlayerIndex(int lastPlayerIndex) {
        mLastPlayerIndex = lastPlayerIndex;
    }

    public void updateScore(DatabaseConnection databaseConnection, int newPoints){
        mScore += newPoints;

        String query = "UPDATE teams SET score = ? WHERE _id = ?";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mScore);
        statement.bindLong(2, mId);

        databaseConnection.executeNonReturn(statement);
    }

    public Player getNextPlayer(){
        mLastPlayerIndex++;
        int newPlayerIndex = mLastPlayerIndex;

        if(newPlayerIndex == mPlayers.size()) {
            newPlayerIndex = 0;
            mLastPlayerIndex = newPlayerIndex;
        }

        for(Player p: mPlayerOrder){
            System.out.println(p.getName());
        }
        return mPlayerOrder.get(newPlayerIndex);
    }

    public void generatePlayerOrder(Random random){
        mLastPlayerIndex = -1;

        List<Integer> usedIndex = new LinkedList<>();

        for (int i = 0; i < mPlayers.size(); i++){
            int playerIndex;
            do {
                playerIndex = random.nextInt(mPlayers.size());
            } while (usedIndex.contains(playerIndex));

            Player player = mPlayers.get(playerIndex);
            mPlayerOrder.add(player);
            usedIndex.add(playerIndex);
        }
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

    @Override
    public int compareTo(@NonNull Team team) {
        if (mScore > team.getScore()) {
            return -1;
        }
        else if (mScore <  team.getScore()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
