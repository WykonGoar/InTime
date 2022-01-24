package com.wykon.intime.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by Wouter on 19-11-2015.
 */
public class DatabaseConnection extends Activity {

    SQLiteDatabase mDatabase;
    Context mContext;
    Logger log;

    //public DatabaseConnection(SQLiteDatabase database, Context context){
    public DatabaseConnection(Context context){
        //mDatabase = database;
        mContext = context;

//        mContext.deleteDatabase("InTimeDB");
        try{
            String check = "SELECT * FROM settings";

            executeReturn(check);
        } catch (SQLiteException ex)
        {
            importDatabaseTables();
        }
    }

    private void importDatabaseTables(){
        try {
            InputStream mInputStream = mContext.getAssets().open("InTimeTables.sql");

            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            String line = null;
            do {
                line = mBufferedReader.readLine();
                System.out.println(line);
                if (line != null) {
                    try {
                        executeNonReturn(line);
                    }
                    catch (SQLiteException ex) {
                        throw new Error(ex.getMessage());
                    }
                }
            } while (line != null);
        }catch (IOException ex){
            throw new Error(ex.getMessage());
        }
    }

    private void createConnection(){
        mDatabase = mContext.openOrCreateDatabase("InTimeDB", MODE_PRIVATE, null);
    }

    public SQLiteStatement getNewStatement(String query){
        createConnection();
        return mDatabase.compileStatement(query);
    }

    public void executeNonReturn(String query) throws SQLiteException {
        createConnection();
        mDatabase.execSQL(query);
        mDatabase.close();
    }

    public void executeNonReturn(SQLiteStatement statement){
        System.out.println(statement.toString());
        statement.execute();
        mDatabase.close();
    }

    public int executeInsertQuery(SQLiteStatement statement){
        int result = (int) statement.executeInsert();
        mDatabase.close();
        return result;
    }

    public int executeUpdateQuery(SQLiteStatement statement){
        int result = statement.executeUpdateDelete();
        mDatabase.close();
        return result;
    }

    public Cursor executeReturn(String query) throws SQLiteException {
        createConnection();
        Cursor mCursor = mDatabase.rawQuery(query, null);
        return mCursor;
    }

    public LinkedList<WordList> getWordLists(boolean inGame){
        String query = "SELECT * FROM lists";

        if (inGame){
            query += " WHERE selected == 1";
        }

        query += " ORDER BY name";

        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        mCursor.moveToFirst();

        LinkedList<WordList> wordLists = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //Id
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //name
            String name = mCursor.getString(mCursor.getColumnIndex("name"));
            //selected
            int iSelected = mCursor.getInt(mCursor.getColumnIndex("selected"));
            boolean selected = false;
            if (iSelected == 1){
                selected = true;
            }

            LinkedList<Word> words = getWords(id, inGame);

            WordList wordList = new WordList(id, name, selected, words);

            wordLists.add(wordList);

            mCursor.moveToNext();
        }

        mDatabase.close();

        return wordLists;
    }

    public LinkedList<Word> getWords(int listId, boolean inGame){
        String query = "SELECT * FROM words WHERE list_id == " + listId;
        if (inGame){
            query += " AND selected == 1";
        }

        query += " ORDER BY word";

        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        mCursor.moveToFirst();

        System.out.println("Amount of words in list: " + mCursor.getCount());

        LinkedList<Word> words = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //wordId
            int wordId = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //word
            String wordValue = mCursor.getString(mCursor.getColumnIndex("word"));
            //selected
            int iSelected = mCursor.getInt(mCursor.getColumnIndex("selected"));
            boolean selected = false;
            if (iSelected == 1){
                selected = true;
            }

            Word word = new Word(wordId, wordValue, selected);
            words.add(word);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return words;
    }

    public Settings getSettings(){
        String query = "SELECT * FROM settings";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
        mCursor.moveToFirst();

        //winPoints
        int winPoints = mCursor.getInt(mCursor.getColumnIndex("win_points"));
        //wordCount
        int wordCount = mCursor.getInt(mCursor.getColumnIndex("word_count"));

        mDatabase.close();
        return new Settings(wordCount, winPoints);
    }

    public void resetWords(){
        String query = "UPDATE words SET used_location = null";
        executeNonReturn(query);
    }

    public LinkedList<String> getFavorites(){
        Cursor mCursor = null;

        try {
            mCursor = executeReturn("SELECT * FROM favorites ORDER BY name");
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        mCursor.moveToFirst();

        LinkedList<String> names = new LinkedList<String>();

        while(!mCursor.isAfterLast()){
            //Name
            String name = mCursor.getString(mCursor.getColumnIndex("name"));

            names.add(name);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return names;
    }

    public LinkedList<Team> getTeams(){
        String query = "SELECT * FROM teams";

        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        mCursor.moveToFirst();

        LinkedList<Team> teams = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //Id
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //name
            String name = mCursor.getString(mCursor.getColumnIndex("name"));
            //score
            int score = mCursor.getInt(mCursor.getColumnIndex("score"));
            //last_player_index
            int last_player_index = mCursor.getInt(mCursor.getColumnIndex("last_player_index"));

            //TODO Order players based on player order
            LinkedList<Player> players = getPlayers(id);
            LinkedList<Player> playerOrder = new LinkedList<Player>();

            Team team = new Team(id, name, score, last_player_index, players, playerOrder);

            teams.add(team);

            mCursor.moveToNext();
        }

        mDatabase.close();

        return teams;

    }

    public LinkedList<Player> getPlayers(int teamId){
        String query = "SELECT * FROM players WHERE team_id == " + teamId + "";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        mCursor.moveToFirst();

        LinkedList<Player> players = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //id
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //name
            String name = mCursor.getString(mCursor.getColumnIndex("name"));

            Player player = new Player(id, name, teamId);
            players.add(player);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return players;
    }

    public Game getLatestGame(){
        String query = "SELECT * FROM games WHERE latest == 1";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
        mCursor.moveToFirst();

        //winPoints
        int winPoints = mCursor.getInt(mCursor.getColumnIndex("win_points"));
        //wordCount
        int wordCount = mCursor.getInt(mCursor.getColumnIndex("word_count"));

        mDatabase.close();

        //TODO Get all values
        return new Game(wordCount, winPoints);
    }
}

