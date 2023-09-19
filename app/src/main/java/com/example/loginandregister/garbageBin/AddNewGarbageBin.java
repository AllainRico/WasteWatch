package com.example.loginandregister.garbageBin;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DatabaseErrorHandler;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.loginandregister.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewGarbageBin extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newGarbageBinText;
    private EditText newGarbageBinPlaceText;
    private Button newGarbinBinSaveButton;


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
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        newGarbageBinText = getView().findViewById(R.id.newBinText);
        newGarbageBinPlaceText = getView().findViewById(R.id.newPlaceText);
        newGarbinBinSaveButton = getView().findViewById(R.id.newBinButton);

        //Naay k butang diri about sa database 8:29
        //not sure gamit to kay sqlite iyang DB



        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String bin = bundle.getString("bin");
            String place = bundle.getString("place");
            newGarbageBinText.setText(bin);
            newGarbageBinPlaceText.setText(place);
            if(bin.length()>0){
                newGarbinBinSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.background_green));
            }
        }

        newGarbageBinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(toString().equals("")){
                    newGarbinBinSaveButton.setEnabled(false);
                    newGarbinBinSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newGarbinBinSaveButton.setEnabled(true);
                    newGarbinBinSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.background_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newGarbageBinPlaceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(toString().equals("")){
                    newGarbinBinSaveButton.setEnabled(false);
                    newGarbinBinSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newGarbinBinSaveButton.setEnabled(true);
                    newGarbinBinSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.background_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newGarbinBinSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String binText = newGarbageBinText.getText().toString();
                    String placeText = newGarbageBinPlaceText.getText().toString();

                    if(finalIsUpdate){
                        //iya k butang diri kay updatetask na method sa sqlite 16:28
                    }
                    else{
                        GarbageBinStatusModel bin = new GarbageBinStatusModel();
                        bin.setBin(binText);
                        bin.setPlace(placeText); //dummy
                        bin.setFillLevel(0);
                    }
                    dismiss();
                }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
