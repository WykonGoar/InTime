package com.wykon.intime.activity.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wykon.intime.R;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Settings;
import com.wykon.intime.model.Word;
import com.wykon.intime.model.WordList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private DatabaseConnection mDatabaseConnection;
    private Settings mSettings;

    private ProgressBar pbTime;
    private TextView tvWord1;
    private TextView tvWord2;
    private TextView tvWord3;
    private TextView tvWord4;
    private TextView tvWord5;
    private TextView tvWord6;

    private ArrayList<Word> mWords = new ArrayList<>();
    private ArrayList<Word> mChosenWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mVisible = true;

        mDatabaseConnection = new DatabaseConnection(this);

        pbTime = findViewById(R.id.pbTime);
        tvWord1 = findViewById(R.id.tvWord1o);
        tvWord2 = findViewById(R.id.tvWord2o);
        tvWord3 = findViewById(R.id.tvWord3o);
        tvWord4 = findViewById(R.id.tvWord4o);
        tvWord5 = findViewById(R.id.tvWord5o);
        tvWord6 = findViewById(R.id.tvWord6);

        mSettings = mDatabaseConnection.getSettings();

        loadWords();
        setWords();
        processUsedWords();

        Thread progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int seconds = 0; seconds <= 60; seconds++)
                {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Passed seconds: " + seconds);

                    pbTime.setProgress(seconds);
                }

                Intent mIntent = new Intent(getApplicationContext(), TimeUpActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
        progressThread.start();
    }

    public void loadWords(){
        LinkedList<WordList> wordLists = mDatabaseConnection.getWordLists(true);

        for (WordList list: wordLists) {
            System.out.println("List name " + list.getName());

            mWords.addAll(list.getWords());
        }
    }

    private void setTvWord(TextView tvWord, int location){
        Word word = getRandomWord();
        mChosenWords.add(word);
        tvWord.setText(word.getWord());
        tvWord.setVisibility(View.VISIBLE);
    }

    public void setWords() {
        setTvWord(tvWord1, 1);
        setTvWord(tvWord2,2);
        setTvWord(tvWord3,3);
        setTvWord(tvWord4,4);

        if (mSettings.getWordCount() == 4)
            return;

        setTvWord(tvWord5,5);

        if (mSettings.getWordCount() == 5)
            return;

        setTvWord(tvWord6,6);
    }

    public Word getRandomWord(){
        Random random = new Random();

        System.out.println("Words size = " + mWords.size());

        int index = 0;
        if (mWords.size() > 1){
            index = random.nextInt(mWords.size() - 1);
        }

        Word chosen = mWords.get(index);
        mWords.remove(index);
        return chosen;
    }

    public void processUsedWords(){
        System.out.println("processUsedWords");
        for(Word word: mChosenWords){
            String query = "UPDATE words SET used_location = ? WHERE _id = ?";
            SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
            statement.bindLong(2, word.getId());
            mDatabaseConnection.executeNonReturn(statement);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
