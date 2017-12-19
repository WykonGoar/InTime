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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wykon.intime.R;
import com.wykon.intime.activity.setup.PlayerActivity;
import com.wykon.intime.adapter.PlayerListAdapter;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Player;
import com.wykon.intime.model.Team;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TeamActivity extends AppCompatActivity {
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

    private int ADD_PLAYER_ID = 1;

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private Team mTeam;
    private EditText etName;
    private ImageView ivAddPlayer;
    private ListView lvPlayers;
    private PlayerListAdapter mPlayersAdapter;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_team);

        mVisible = true;

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        Intent mIntent = getIntent();
        if(mIntent.hasExtra("Team")){
            mTeam = (Team) mIntent.getSerializableExtra("Team");
        }
        else {
            mTeam = new Team();
        }

        etName = findViewById(R.id.etName);
        ivAddPlayer = findViewById(R.id.ivAddPlayer);
        lvPlayers = findViewById(R.id.lvPlayers);
        bSave = findViewById(R.id.bSave);

        etName.setText(mTeam.getName());

        mPlayersAdapter = new PlayerListAdapter(this, mTeam.getPlayers());
        lvPlayers.setAdapter(mPlayersAdapter);

        ivAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent mIntent = new Intent(getApplicationContext(), PlayerActivity.class);
            mIntent.putExtra("Team", mTeam);

            startActivityForResult(mIntent, ADD_PLAYER_ID);
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

                for (Team team : mDatabaseConnection.getTeams()){
                    if (team.getId() != mTeam.getId() && team.getName().equals(name)){
                        Toast.makeText(mContext, "Team met de naam '" + name + "' bestaat al", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                mTeam.setName(name);
                mTeam.save(mDatabaseConnection);

                Intent result = new Intent();
                result.putExtra("Team", mTeam);
                setResult(RESULT_OK, result);
                finish();
                }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PLAYER_ID){
            if (resultCode == RESULT_OK){
                String name = data.getStringExtra("Name");

                List<Player> players = mTeam.getPlayers();
                players.add(new Player(name));
                mPlayersAdapter.notifyDataSetChanged();
            }
        }
    }
}
