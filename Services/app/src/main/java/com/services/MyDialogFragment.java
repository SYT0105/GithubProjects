package com.services;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.providers.R;

public class MyDialogFragment extends DialogFragment {

    private ListView listView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("My Title");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Sample 1");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");

        listView.setAdapter(arrayAdapter);

    }
}