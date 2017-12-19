package com.wykon.intime.activity.setup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.wykon.intime.R;
import com.wykon.intime.activity.MainActivity;
import com.wykon.intime.activity.setup.WordListActivity;
import com.wykon.intime.adapter.WordListListAdapter;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.WordList;

import java.util.LinkedList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WordListsActivity extends AppCompatActivity {
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

    private int ADD_WORD_LIST_ID = 1;
    private int EDIT_WORD_LIST_ID = 2;

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private ImageView ivHome;
    private ImageView ivAddList;
    private ListView lvWordLists;
    private WordListListAdapter mWordListListAdapter;
    private LinkedList<WordList> mWordLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_lists);

        mVisible = true;

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        ivHome = findViewById(R.id.ivHome);
        ivAddList = findViewById(R.id.ivAddList);
        lvWordLists = findViewById(R.id.lvWordLists);

        mWordLists = mDatabaseConnection.getWordLists(false);
        mWordListListAdapter = new WordListListAdapter(this, mWordLists);
        lvWordLists.setAdapter(mWordListListAdapter);

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        ivAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, WordListActivity.class);

                startActivityForResult(mIntent, ADD_WORD_LIST_ID);
            }
        });
    }

    public void onEditWordListClick(WordList wordList){
        Intent mIntent = new Intent(mContext, WordListActivity.class);
        mIntent.putExtra("WordList", wordList);

        startActivityForResult(mIntent, EDIT_WORD_LIST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == ADD_WORD_LIST_ID){
            WordList wordList = (WordList) data.getSerializableExtra("WordList");

            mWordLists.add(wordList);
            mWordListListAdapter.notifyDataSetChanged();
        }
        else if (requestCode == EDIT_WORD_LIST_ID){
            WordList updatedWordList = (WordList) data.getSerializableExtra("WordList");

            int listPosition = 0;

            for (int index = 0; index < mWordLists.size(); index++){
                WordList wordList = mWordLists.get(index);
                if (wordList.getId() == updatedWordList.getId()) {
                    listPosition = index;
                    break;
                }
            }

            mWordLists.remove(listPosition);
            mWordLists.add(listPosition, updatedWordList);
            mWordListListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
