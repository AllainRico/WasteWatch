package com.example.loginandregister.garbageBin;

import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.loginandregister.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewGarbageBin extends BottomSheetDialogFragment {
    public static final String Tag = "ActionBottomDialog";

    private EditText newGarbageBinText;
    private Button newGarbinBinSaveButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    public static AddNewGarbageBin newInstance(){
        return new AddNewGarbageBin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.new_bin, container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }
}
