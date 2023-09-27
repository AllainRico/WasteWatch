package com.example.loginandregister.garbageBin;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginandregister.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
public class GarbageBinStatus extends Fragment implements DialogCloseListener {

    private Button backButton;
    private FloatingActionButton addGarbageBin;
    private RecyclerView garbageBinRecyclerView;
    private GarbageBinStatusAdapter garbageBinAdapter;
    private List<GarbageBinStatusModel> garbageBinList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_garbage_bin_status, container, false);

        initWidgets(view);
        garbageBinList = new ArrayList<>();

        garbageBinRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        garbageBinAdapter = new GarbageBinStatusAdapter(this);
        garbageBinRecyclerView.setAdapter(garbageBinAdapter);

        Collections.reverse(garbageBinList);
        garbageBinAdapter.setBin(garbageBinList);

        //dummy
        GarbageBinStatusModel model = new GarbageBinStatusModel();
        model.setBin("Test Bin");
        model.setFillLevel(0);

        garbageBinList.add(model);

        garbageBinAdapter.setBin(garbageBinList);

        addGarbageBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewGarbageBin.newInstance(garbageBinList, garbageBinAdapter).show(getParentFragmentManager(), AddNewGarbageBin.TAG);
            }
        });

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initWidgets(View view){
        backButton = view.findViewById(R.id.backButton);
        garbageBinRecyclerView = view.findViewById(R.id.garbageBinRecyclerView);
        addGarbageBin = view.findViewById(R.id.addGarbageBin);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Collections.reverse(garbageBinList);
        garbageBinAdapter.setBin(garbageBinList);
        garbageBinAdapter.notifyDataSetChanged();
    }
}
