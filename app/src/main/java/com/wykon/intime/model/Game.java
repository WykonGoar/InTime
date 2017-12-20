package com.wykon.intime.model;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by 52 on 29-11-2017.
 */

public class Game implements Serializable{
    private int mId;
    private int mWordCount;
    private int mWinPoints;
    private int mLastTeamIndex = -1;
    private boolean mLatest;
    private boolean mFinished;

    private LinkedList<Team> mTeamOrder = new LinkedList<>();

    private LinkedList<Word> mAllWords = new LinkedList<>();
    private LinkedList<Word> mNewWords = new LinkedList<>();
    private LinkedList<Word> mUsedWords = new LinkedList<>();
    private LinkedList<Word> mLastUsedWords;

    public Game(int wordCount, int winPoints){
        mWordCount = wordCount;
        mWinPoints = winPoints;


    }

    public Game(int id, int wordCount, int winPoints){
        mId = id;
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

    public Team getTeam(int id){
        for (Team team: mTeamOrder){
            if (team.getId() == id)
                return team;
        }

        return null;
    }

    public Player getNextPlayer(){
        mLastTeamIndex++;
        int newTeamIndex = mLastTeamIndex;
        System.out.println("Team size = " + mTeamOrder.size());

        if(newTeamIndex == mTeamOrder.size()) {
            newTeamIndex = 0;
            mLastTeamIndex = newTeamIndex;
        }

        System.out.println("Team index = " + mLastTeamIndex);
        return mTeamOrder.get(mLastTeamIndex).getNextPlayer();
    }

    public LinkedList<Word> getLastUsedWords() {
        return mLastUsedWords;
    }

    public List<Team> getTeams(){
        return mTeamOrder;
    }

    public void setWordCount(int wordCount) {
        mWordCount = wordCount;
    }

    public void setWinPoints(int winPoints) {
        mWinPoints = winPoints;
    }

    public void finished(DatabaseConnection databaseConnection){
        mFinished = true;

        String finishedQuery = "UPDATE games SET finished = 1 WHERE _id = ?";
        SQLiteStatement statement = databaseConnection.getNewStatement(finishedQuery);
        statement.bindLong(1, mId);
    }

    public void insert(DatabaseConnection databaseConnection){
        String sTeamOrder = "";

        for (int index = 0; index < mTeamOrder.size(); index++){
            Team team = mTeamOrder.get(index);
            sTeamOrder += team.getId() + ";";
        }

        //Make sure no game is last
        String noLatestQuery = "UPDATE games SET latest = 0";
        databaseConnection.executeNonReturn(noLatestQuery);

        //Insert game
        String query = "INSERT INTO games(win_points, word_count, team_order, last_team_index, latest) VALUES(?,?,?,0,1)";

        SQLiteStatement statement = databaseConnection.getNewStatement(query);
        statement.bindLong(1, mWinPoints);
        statement.bindLong(2, mWordCount);
        statement.bindString(3, sTeamOrder);

        mId = databaseConnection.executeInsertQuery(statement);
    }

    public LinkedList<Word> generate_random_words(){
        Random random = new Random();
        System.out.println("Words size = " + mNewWords.size());

        mLastUsedWords = new LinkedList<>();
        for (int i = 0; i < mWordCount; i++){
            if (mNewWords.size() == 0){
                mNewWords = new LinkedList<Word>(mAllWords);
            }

            Word newWord;
            int index;
            do {
                index = random.nextInt(mNewWords.size());
                newWord = mNewWords.get(index);
            } while (mLastUsedWords.contains(newWord));

            mLastUsedWords.add(newWord);
            mNewWords.remove(index);
        }

        return mLastUsedWords;
    }

    public boolean validateGameRequirements(Context context, DatabaseConnection databaseConnection){
        //Validate teams and team sizes
        LinkedList<Team> teams = databaseConnection.getTeams();
        if (teams.size() <= 1){
            Toast.makeText(context, "Er moet minimaal 2 teams zijn", Toast.LENGTH_LONG).show();
            return false;
        }

        for (Team team: teams){
            if (team.getPlayers().size() < 1){
                Toast.makeText(context, "Een team moet minimaal 1 speler hebben", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        //Validate wordLists and words sizes
        LinkedList<WordList> wordLists = databaseConnection.getWordLists(true);
        int totalWords = 0;
        if (wordLists.size() < 1){
            Toast.makeText(context, "Er moet minimaal 1 woorden lijst geselecteed zijn", Toast.LENGTH_LONG).show();
            return false;
        }

        for (WordList wordList: wordLists){
            System.out.println("for loop Amount words in list: " + wordList.getWords().size());
            totalWords += wordList.getWords().size();
        }

        System.out.println("Amount of words = " + totalWords);
        if (totalWords < mWordCount){
            Toast.makeText(context, "Er moet minimaal " + mWordCount + " woorden geselecteerd zijn", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void createGameValues(DatabaseConnection databaseConnection){
        LinkedList<Team> teams = databaseConnection.getTeams();
        List<Integer> usedIndex = new LinkedList<>();
        mLastTeamIndex = -1;

        Random random = new Random();
        for (int i = 0; i < teams.size(); i++){
            int teamIndex;
            do {
                teamIndex = random.nextInt(teams.size());
            } while (usedIndex.contains(teamIndex));

            Team team = teams.get(teamIndex);
            team.resetScore(databaseConnection);
            team.generatePlayerOrder(random);

            mTeamOrder.add(team);
            usedIndex.add(teamIndex);
        }

        LinkedList<WordList> wordLists = databaseConnection.getWordLists(true);
        for (WordList wordList: wordLists){
            mAllWords.addAll(wordList.getWords());
        }

        mNewWords = new LinkedList<Word>(mAllWords);

        insert(databaseConnection);
    }
}
