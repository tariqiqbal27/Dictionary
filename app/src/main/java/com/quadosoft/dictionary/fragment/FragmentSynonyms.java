package com.quadosoft.dictionary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quadosoft.dictionary.R;
import com.quadosoft.dictionary.WordMeaningActivity;

public class FragmentSynonyms extends Fragment {

    public FragmentSynonyms() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition,container,false);

        Context context = getActivity();
        TextView text = view.findViewById(R.id.textViewD);

        String synonyms = ((WordMeaningActivity)context).synonyms;
        if(synonyms!=null) {
            synonyms = synonyms.replaceAll(",","\n");
            text.setText(synonyms);
        }

        if(synonyms==null) {
            text.setText("Synonyms Not Found");
        }

        return view;

    }
}
