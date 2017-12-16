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
import com.wykon.intime.model.Word;

import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class WordAdapter extends BaseAdapter {

    private Context mContext;
    private List<Word> mWords;

    public WordAdapter(Context context, List<Word> word) {
        mContext = context;
        mWords = word;
    }

    @Override
    public int getCount() {
        return mWords.size();
    }

    @Override
    public Object getItem(int position) {
        return mWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mWords.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.row_name, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        ImageView ivDelete = rowView.findViewById(R.id.ivDelete);

        final Word word = mWords.get(position);
        tvName.setText(word.getWord());

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(mContext);
                confirm.setMessage("Weet je zeker dat je '" + word.getWord() + "' wilt verwijderen?");

                confirm.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWords.remove(position);
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
                editName.setMessage("Woord aanpassen");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                input.setText(word.getWord());
                editName.setView(input);

                editName.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newWord = input.getText().toString();
                        newWord = newWord.trim();
                        if (newWord.equals(""))
                            return;

                        for(int index = 0; index < mWords.size(); index ++)
                        {
                            if (index == position)
                                continue;

                            Word iWord = mWords.get(index);
                            if (iWord.getWord().equals(newWord)){
                                Toast.makeText(mContext, "'" + newWord + "' is al toegevoegd", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        word.setWord(newWord);
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
