package com.wykon.intime.activity.setup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wykon.intime.R;
import com.wykon.intime.adapter.WordListAdapter;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Word;
import com.wykon.intime.model.WordList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WordListActivity extends AppCompatActivity {
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

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private WordList mWordList;
    private EditText etName;
    private ImageView ivAddWord;
    private ListView lvWords;
    private WordListAdapter mWordListAdapter;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_list);

        mVisible = true;

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        Intent mIntent = getIntent();
        if(mIntent.hasExtra("WordList")){
            mWordList = (WordList) mIntent.getSerializableExtra("WordList");
        }
        else {
            mWordList = new WordList();
        }

        etName = findViewById(R.id.etName);
        ivAddWord = findViewById(R.id.ivAddWord);
        lvWords = findViewById(R.id.lvWords);
        bSave = findViewById(R.id.bSave);

        etName.setText(mWordList.getName());

        mWordListAdapter = new WordListAdapter(this, mWordList.getWords());
        lvWords.setAdapter(mWordListAdapter);

        ivAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        ivAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder addName = new AlertDialog.Builder(mContext);
                addName.setMessage("Woord toevoegen");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                addName.setView(input);

                addName.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sNewWord = input.getText().toString();
                        sNewWord = sNewWord.trim();
                        if (sNewWord.equals(""))
                            return;

                        for (Word word: mWordList.getWords()){
                            if (word.getWord().equals(sNewWord)){
                                Toast.makeText(mContext, "'" + sNewWord + "' is al toegevoegd", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        Word newWord = new Word(sNewWord);
                        mWordList.getWords().add(newWord);

                        mWordListAdapter.notifyDataSetChanged();
                    }
                });

                addName.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                addName.create().show();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                name = name.trim();
                if (name.equals("")){
                    Toast.makeText(mContext, "Geen naam ingevuld", Toast.LENGTH_LONG).show();
                    return;
                }

                for (WordList wordList: mDatabaseConnection.getWordLists(false)){
                    if (wordList.getId() != mWordList.getId() && wordList.getName().equals(name)){
                        Toast.makeText(mContext, "Lijst met de naam '" + name + "' bestaat al", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                mWordList.setName(name);
                mWordList.save(mDatabaseConnection);

                Intent result = new Intent();
                result.putExtra("WordList", mWordList);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
