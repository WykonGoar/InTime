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

        try{
            String check = "SELECT * FROM lists";

            executeReturn(check);
        } catch (SQLiteException ex)
        {
            importDatabaseTables();
            importWordLists();
        }
    }

    private void importDatabaseTables(){
        try {
            InputStream mInputStream = mContext.getAssets().open("InTimeTables.sql");

            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            String line = null;
            do {
                line = mBufferedReader.readLine();
                System.out.println("readed line");
                System.out.println(line);
                if (line != null) {
                    executeNonReturn(line);
                }
            } while (line != null);
        }catch (IOException ex){
            System.out.println("Exception raised");
            System.out.println(ex.getMessage());
            throw new Error(ex.getMessage());
        }
    }

    private void importWordLists(){
        try {
            System.out.println("IMPORT WORD LISTS");
            InputStream mInputStream = mContext.getAssets().open("NewWordLists.sql");

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

            String wordQuery = "SELECT * FROM words WHERE list_id == " + id + " ORDER BY word";
            LinkedList<Word> words = getWords(wordQuery);

            WordList wordList = new WordList(id, name, selected, words);

            wordLists.add(wordList);

            mCursor.moveToNext();
        }

        mDatabase.close();

        return wordLists;
    }

    public LinkedList<Word> getWords(String query)
    {
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        mCursor.moveToFirst();

        LinkedList<Word> words = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //wordId
            int wordId = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //word
            String wordValue = mCursor.getString(mCursor.getColumnIndex("word"));
            //usedLocation
            int usedLocation = mCursor.getInt(mCursor.getColumnIndex("used_location"));

            Word word = new Word(wordId, wordValue, usedLocation);
            words.add(word);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return words;
    }

    public Game getGame()
    {
        String query = "SELECT * FROM games";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new Game();
        }
        mCursor.moveToFirst();

        System.out.println("loop cursor");
        Game game = new Game();
        //winPoints
        int winPoints = mCursor.getInt(mCursor.getColumnIndex("win_points"));
        //wordCount
        int wordCount = mCursor.getInt(mCursor.getColumnIndex("word_count"));
        //score
        int score = mCursor.getInt(mCursor.getColumnIndex("score"));
        //mode
        String mode = mCursor.getString(mCursor.getColumnIndex("mode"));

        System.out.println("set values of game");

        game.setWinPoints(winPoints);
        game.setWordCount(wordCount);
        game.setScore(score);
        game.setMode(mode);

        mDatabase.close();
        return game;
    }

    public LinkedList<Word> getUsedWords(){
        String query = "SELECT * FROM words WHERE used_location != -1";
        return getWords(query);
    }

    public void resetWords(){
        String query = "UPDATE words SET used_location = null";
        executeNonReturn(query);
    }

    public void resetGame(){
        String clearGameQuery = "UPDATE games SET score = 0";
        String clearUsedWords = "UPDATE words SET used_location = null";

        executeNonReturn(clearGameQuery);
        executeNonReturn(clearUsedWords);

    }
}
