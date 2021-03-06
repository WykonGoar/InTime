package com.wykon.intime.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wykon.intime.R;

import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class FavoriteListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mFavorites;

    public FavoriteListAdapter(Context context, List<String> favorites) {
        mContext = context;
        mFavorites = favorites;
    }

    @Override
    public int getCount() {
        return mFavorites.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFavorites.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_name, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        ImageView ivDelete = rowView.findViewById(R.id.ivDelete);

        final String favorite = mFavorites.get(position);
        tvName.setText(favorite);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
                confirm.setMessage("Weet je zeker dat je '" + favorite + "' wilt verwijderen?");

                confirm.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFavorites.remove(position);
                        notifyDataSetChanged();
                    }
                });

                confirm.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                confirm.create().show();
            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder editName = new AlertDialog.Builder(mContext);
                editName.setMessage("Naam aanpassen");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                input.setText(favorite);
                editName.setView(input);

                editName.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = input.getText().toString();
                        newName = newName.trim();
                        if (newName.equals(""))
                            return;

                        for(int index = 0; index < mFavorites.size(); index ++)
                        {
                            if (index == position)
                                continue;

                            String iFavorite = mFavorites.get(index);
                            if (iFavorite.equals(newName)){
                                Toast.makeText(mContext, "'" + newName + "' is al toegevoegd", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        mFavorites.remove(position);
                        mFavorites.add(position, favorite);
                        
                        notifyDataSetChanged();
                    }
                });

                editName.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                editName.create().show();
            }
        });

        return  rowView;
    }
}
