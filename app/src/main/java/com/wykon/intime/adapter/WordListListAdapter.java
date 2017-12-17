package com.wykon.intime.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wykon.intime.R;
import com.wykon.intime.activity.WordListsActivity;
import com.wykon.intime.model.DatabaseConnection;
import com.wykon.intime.model.WordList;

import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class WordListListAdapter extends BaseAdapter {

    private WordListsActivity mContext;
    private DatabaseConnection mDatabaseConnection;
    private List<WordList> mWordLists;

    public WordListListAdapter(WordListsActivity context, List<WordList> wordLists) {
        mContext = context;
        mWordLists = wordLists;
        mDatabaseConnection = new DatabaseConnection(context);
    }

    @Override
    public int getCount() {
        return mWordLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mWordLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mWordLists.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_word_list, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        final CheckBox cbSelected = rowView.findViewById(R.id.cbSelected);
        ImageView ivDelete = rowView.findViewById(R.id.ivDelete);

        final WordList wordList = mWordLists.get(position);
        cbSelected.setChecked(wordList.isSelected());
        tvName.setText(wordList.getName());

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
                confirm.setMessage("Weet je zeker dat je '" + wordList.getName() + "' wilt verwijderen?");

                confirm.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWordLists.remove(position);
                        wordList.delete(mDatabaseConnection);
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
                mContext.onEditWordListClick(wordList);
            }
        });

        cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newChecked = cbSelected.isChecked();

                cbSelected.setChecked(newChecked);
                wordList.setSelected(newChecked);
                wordList.save(mDatabaseConnection);
                notifyDataSetChanged();
            }
        });

        return  rowView;
    }
}
