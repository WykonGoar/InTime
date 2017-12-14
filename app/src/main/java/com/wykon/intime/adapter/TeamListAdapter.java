package com.wykon.intime.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wykon.intime.R;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.Player;
import com.wykon.intime.model.Team;

import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class TeamListAdapter extends BaseAdapter {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private List<Team> mTeams;

    public TeamListAdapter(Context context, DatabaseConnection databaseConnection, List<Team> teams) {
        mContext = context;
        mDatabaseConnection = databaseConnection;
        mTeams = teams;
    }

    @Override
    public int getCount() {
        return mTeams.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTeams.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_team, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        TextView tvPlayersCount = rowView.findViewById(R.id.tvPlayersCount);
        ImageView ivDelete = rowView.findViewById(R.id.ivDelete);

        final Team team = mTeams.get(position);
        tvName.setText(team.getName());
        tvPlayersCount.setText(team.getPlayers().size() + " spelers");

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
                confirm.setMessage("Weet je zeker dat je '" + team.getName() + "' wilt verwijderen?");

                confirm.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTeams.remove(position);
                        team.delete(mDatabaseConnection);
                        notifyDataSetChanged();
                    }
                });

                confirm.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                confirm.create().show();
            }
        });

        return  rowView;
    }
}
