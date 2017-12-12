package com.wykon.intime.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wykon.intime.R;
import com.wykon.intime.adapter.PlayerListAdapter;
import com.wykon.intime.adapter.TeamListAdapter;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Game;
import com.wykon.intime.model.Player;
import com.wykon.intime.model.Team;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NewGameActivity extends AppCompatActivity {
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

    private int ADD_TEAM_ID = 1;
    private int UPDATE_TEAM_ID = 2;

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private Game mGame;
    private ImageView ivHome;
    private TextView tvWinPoints;
    private TextView tvWordCount;
    private ListView lvTeams;
    private TeamListAdapter mTeamsAdapter;
    private ImageView ivAddTeam;

    private List<Team> mTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game);

        mVisible = true;
        mContext = this;

        mDatabaseConnection = new DatabaseConnection(this);
        mGame = mDatabaseConnection.getGame();

        ivHome = findViewById(R.id.ivHome);
        tvWinPoints = findViewById(R.id.tvWinPoints);
        tvWordCount = findViewById(R.id.tvWordCount);
        lvTeams = findViewById(R.id.lvTeams);
        ivAddTeam = findViewById(R.id.ivAddTeam);

        tvWinPoints.setText("" + mGame.getWinPoints());
        tvWordCount.setText("" + mGame.getWordCount());

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);
            }
        });

        ivAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), TeamActivity.class);
                startActivityForResult(mIntent, ADD_TEAM_ID);
            }
        });

        lvTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Team team = (Team) mTeamsAdapter.getItem(position);

                Intent mIntent = new Intent(getApplicationContext(), TeamActivity.class);
                mIntent.putExtra("Team", team);
                startActivityForResult(mIntent, UPDATE_TEAM_ID);
            }
        });

        LoadTeams();
    }

    private void LoadTeams(){
        mTeams = mDatabaseConnection.getTeams();

        mTeamsAdapter = new TeamListAdapter(this, mDatabaseConnection, mTeams);
        lvTeams.setAdapter(mTeamsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TEAM_ID){
            if (resultCode == RESULT_OK){
                Team team = (Team) data.getSerializableExtra("Team");

                mTeams.add(team);
                mTeamsAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == UPDATE_TEAM_ID){
            if (resultCode == RESULT_OK){
                Team updatedTeam = (Team) data.getSerializableExtra("Team");
                for (Team team : mTeams){
                    if (team.getId() == updatedTeam.getId()){
                        team.setName(updatedTeam.getName());
                        team.setPlayers(updatedTeam.getPlayers());
                        break;
                    }
                }
                mTeamsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
