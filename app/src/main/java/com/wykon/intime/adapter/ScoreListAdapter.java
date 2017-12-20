package com.wykon.intime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wykon.intime.R;
import com.wykon.intime.model.Team;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class ScoreListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Team> mTeamScores;

    public ScoreListAdapter(Context context, List<Team> teamScores) {
        mContext = context;
        mTeamScores = new LinkedList<Team>(teamScores);
        Collections.sort(mTeamScores);
    }

    @Override
    public int getCount() {
        return mTeamScores.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeamScores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTeamScores.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_score, parent, false);

        TextView tvPosition = rowView.findViewById(R.id.tvPosition);
        TextView tvName = rowView.findViewById(R.id.tvName);
        TextView tvScore = rowView.findViewById(R.id.tvScore);

        Team team = mTeamScores.get(position);
        System.out.println(team.getName() + "  score = " + team.getScore());
        tvPosition.setText("" + (position + 1));
        tvName.setText(team.getName());
        tvScore.setText("" + team.getScore());

        return  rowView;
    }
}
