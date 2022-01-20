package com.wykon.intime.activity.setup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.wykon.intime.R;
import com.wykon.intime.activity.MainActivity;
import com.wykon.intime.activity.game.GameActivity;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Game;
import com.wykon.intime.model.Settings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingsActivity extends AppCompatActivity {
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
            // Delayed removal of status and navigation bar

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
    private ImageView ivHome;
    private Spinner sWinPoints;
    private Spinner sWordCount;
    private Button bWords;
    private Button bFavorites;
    private Button bStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        mVisible = true;

        mDatabaseConnection = new DatabaseConnection(this);
        ivHome = findViewById(R.id.ivHome);
        sWinPoints = findViewById(R.id.sWinPoints);
        sWordCount = findViewById(R.id.sWordCount);
        bWords = findViewById(R.id.bWords);
        bFavorites = findViewById(R.id.bFavorites);
        bStart = findViewById(R.id.bStart);

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);
            }
        });


        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game newGame = new Game(mDatabaseConnection, mSettings.getWordCount(), mSettings.getWinPoints());

                Intent mIntent = new Intent(getApplicationContext(), GameActivity.class);
                mIntent.putExtra("Game", newGame);

                startActivity(mIntent);
            }
        });

        ArrayAdapter<CharSequence> winPointsAdapter = ArrayAdapter.createFromResource(this,
                R.array.win_points, android.R.layout.simple_dropdown_item_1line);
        winPointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sWinPoints.setAdapter(winPointsAdapter);

        ArrayAdapter<CharSequence> wordCountAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_count, android.R.layout.simple_dropdown_item_1line);
        wordCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sWordCount.setAdapter(wordCountAdapter);

        mSettings = mDatabaseConnection.getSettings();
        int winPointIndex = winPointsAdapter.getPosition("" + mSettings.getWinPoints());
        int wordCountIndex = wordCountAdapter.getPosition("" + mSettings.getWordCount());

        sWinPoints.setSelection(winPointIndex);
        sWordCount.setSelection(wordCountIndex);

        sWinPoints.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedWinPoints = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
                mSettings.setWinPoints(selectedWinPoints);

                mSettings.save(mDatabaseConnection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sWordCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedWordCount = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
                mSettings.setWordCount(selectedWordCount);

                mSettings.save(mDatabaseConnection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), WordListsActivity.class);
                startActivity(mIntent);
            }
        });

        bFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), FavoritesActivity.class);
                startActivity(mIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
