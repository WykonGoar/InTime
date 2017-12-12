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
import com.wykon.intime.model.Player;

import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class PlayerListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Player> mPlayers;

    public PlayerListAdapter(Context context, List<Player> players) {
        mContext = context;
        mPlayers = players;
    }

    @Override
    public int getCount() {
        return mPlayers.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mPlayers.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_player, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        ImageView ivDelete = rowView.findViewById(R.id.ivDelete);

        final Player player = mPlayers.get(position);
        tvName.setText(player.getName());

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
                confirm.setMessage("Weet je zeker dat je " + player.getName() + " wilt verwijderen?");

                confirm.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPlayers.remove(position);
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
