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
import com.wykon.intime.model.Game;
import com.wykon.intime.model.Player;
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

    private Game mGame;
    private Player mPlayer;

    private ProgressBar pbTime;
    private TextView tvWord1;
    private TextView tvWord2;
    private TextView tvWord3;
    private TextView tvWord4;
    private TextView tvWord5;
    private TextView tvWord6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mVisible = true;

        Intent mIntent = getIntent();
        mGame = (Game) mIntent.getSerializableExtra("Game");
        mPlayer = (Player) mIntent.getSerializableExtra("Player");

        pbTime = findViewById(R.id.pbTime);
        tvWord1 = findViewById(R.id.tvWord1o);
        tvWord2 = findViewById(R.id.tvWord2o);
        tvWord3 = findViewById(R.id.tvWord3o);
        tvWord4 = findViewById(R.id.tvWord4o);
        tvWord5 = findViewById(R.id.tvWord5o);
        tvWord6 = findViewById(R.id.tvWord6);

        setWords();

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

                    pbTime.setProgress(seconds);
                }

                Intent mIntent = new Intent(getApplicationContext(), TimeUpActivity.class);
                mIntent.putExtra("Game", mGame);
                mIntent.putExtra("Player", mPlayer);
                startActivity(mIntent);
                finish();
            }
        });
        progressThread.start();
    }

    private void setTvWord(TextView tvWord, Word word){
        tvWord.setText(word.getWord());
        tvWord.setVisibility(View.VISIBLE);
    }

    public void setWords() {
        LinkedList<Word> words = mGame.generate_random_words();

        setTvWord(tvWord1, words.get(0));
        setTvWord(tvWord2, words.get(1));
        setTvWord(tvWord3, words.get(2));
        setTvWord(tvWord4, words.get(3));

        if (mGame.getWordCount() == 4)
            return;

        setTvWord(tvWord5, words.get(4));

        if (mGame.getWordCount() == 5)
            return;

        setTvWord(tvWord6, words.get(5));
    }

    @Override
    public void onBackPressed() {

    }
}
