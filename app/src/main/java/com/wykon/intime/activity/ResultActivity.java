package com.wykon.intime.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Paint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;

import com.wykon.intime.R;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Settings;
import com.wykon.intime.model.Word;

import java.util.LinkedList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ResultActivity extends AppCompatActivity {
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

    private Button bContinue;
    private CheckedTextView ctvWord1;
    private CheckedTextView ctvWord2;
    private CheckedTextView ctvWord3;
    private CheckedTextView ctvWord4;
    private CheckedTextView ctvWord5;
    private CheckedTextView ctvWord6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        mVisible = true;

        mDatabaseConnection = new DatabaseConnection(this);

        bContinue = findViewById(R.id.bContinue);
        ctvWord1 = findViewById(R.id.ctvWord1);
        ctvWord2 = findViewById(R.id.ctvWord2);
        ctvWord3 = findViewById(R.id.ctvWord3);
        ctvWord4 = findViewById(R.id.ctvWord4);
        ctvWord5 = findViewById(R.id.ctvWord5);
        ctvWord6 = findViewById(R.id.ctvWord6);

        mSettings = mDatabaseConnection.getSettings();

        loadWords();
        setOnClickListeners();

        mDatabaseConnection.resetWords();

        bContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = null;

                startActivity(mIntent);
                finish();
            }
        });

    }

    public int getNewPoints(){
        int score = 0;

        if(ctvWord1.isChecked())
            score++;
        if(ctvWord2.isChecked())
            score++;
        if(ctvWord3.isChecked())
            score++;
        if(ctvWord4.isChecked())
            score++;
        if(ctvWord5.isChecked())
            score++;
        if(ctvWord6.isChecked())
            score++;

        if(score == mSettings.getWordCount())
            score++;

        return score;
    }

    public void updateScore(int newScore){
        String query = "UPDATE games SET score = ?";
        SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
        statement.bindLong(1, newScore);
        mDatabaseConnection.executeNonReturn(statement);
    }

    public void loadWords(){
        LinkedList<Word> wordLists = mDatabaseConnection.getUsedWords();

        for (Word word: wordLists) {

            /*
            switch (word.getUsedLocation()) {
                case 1:
                    ctvWord1.setText(word.getWord());
                    break;
                case 2:
                    ctvWord2.setText(word.getWord());
                    break;
                case 3:
                    ctvWord3.setText(word.getWord());
                    break;
                case 4:
                    ctvWord4.setText(word.getWord());
                    break;
                case 5:
                    ctvWord5.setText(word.getWord());
                    ctvWord5.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    ctvWord6.setText(word.getWord());
                    ctvWord6.setVisibility(View.VISIBLE);
                    break;
            }
            */
        }
    }

    public void updateCheckedTextView(CheckedTextView ctvWord){
        if (ctvWord.isChecked()) {
            ctvWord.setChecked(false);
            ctvWord.setPaintFlags(0);
        }
        else {
            ctvWord.setChecked(true);
            ctvWord.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setOnClickListeners(){
        ctvWord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateCheckedTextView(ctvWord1);

            }
        });
        ctvWord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckedTextView(ctvWord2);
            }
        });
        ctvWord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckedTextView(ctvWord3);
            }
        });
        ctvWord4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckedTextView(ctvWord4);
            }
        });
        ctvWord5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckedTextView(ctvWord5);
            }
        });
        ctvWord6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckedTextView(ctvWord6);
            }
        });
    }


    @Override
    public void onBackPressed() {

    }

}
