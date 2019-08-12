package com.quadosoft.dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerVIewAdapterHistory extends RecyclerView.Adapter<RecyclerVIewAdapterHistory.HistoryViewHolder> {
    private ArrayList<History> histories;
    private Context context;

    public RecyclerVIewAdapterHistory(ArrayList<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item_views,viewGroup,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int i) {
        historyViewHolder.en_word.setText(histories.get(i).getEn_word());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView en_word;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            en_word = itemView.findViewById(R.id.en_word);
            itemView.setOnClickListener((v)-> {
                int position = getAdapterPosition();
                String text = histories.get(position).getEn_word();

                Intent intent = new Intent(context,WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",text);
                intent.putExtras(bundle);
                context.startActivity(intent);

            });
        }
    }
}
