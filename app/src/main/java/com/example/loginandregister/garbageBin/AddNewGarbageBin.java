package com.example.loginandregister.garbageBin;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.example.loginandregister.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class AddNewGarbageBin extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newGarbageBinText;
    private Button newGarbageBinSaveButton;
    private List<GarbageBinStatusModel> garbageBinList;
    private GarbageBinStatusAdapter garbageBinAdapter;
    public static AddNewGarbageBin newInstance(List<GarbageBinStatusModel> garbageBinList, GarbageBinStatusAdapter garbageBinAdapter) {
        AddNewGarbageBin fragment = new AddNewGarbageBin();
        fragment.garbageBinList = garbageBinList;
        fragment.garbageBinAdapter = garbageBinAdapter;
        return fragment;
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

        newGarbageBinText = view.findViewById(R.id.newBinText);
        newGarbageBinSaveButton = view.findViewById(R.id.newBinButton);

        newGarbageBinSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String binText = newGarbageBinText.getText().toString();
                if (!binText.isEmpty()) {
                    GarbageBinStatusModel newBin = new GarbageBinStatusModel();
                    newBin.setBin(binText);
                    newBin.setFillLevel(0);

                    garbageBinList.add(newBin);
                    garbageBinAdapter.setBin(garbageBinList);
                    // Log list size before and after adding a new bin
                    Log.d("DEBUG", "List size before adding: " + garbageBinList.size());

                    // Notify the adapter that the data has changed
                    garbageBinAdapter.notifyDataSetChanged();

                    // Log list size after adding
                    Log.d("DEBUG", "List size after adding: " + garbageBinList.size());

                    // Close the dialog
                    Toast.makeText(getActivity(), "Bin Added", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Bin Text is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String bin = bundle.getString("bin");
            newGarbageBinText.setText(bin);
            if(bin.length()>0){
                newGarbageBinSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.background_green));
            }
        }

        newGarbageBinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()){
                    newGarbageBinSaveButton.setEnabled(false);
                    newGarbageBinSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newGarbageBinSaveButton.setEnabled(true);
                    newGarbageBinSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newGarbageBinSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String binText = newGarbageBinText.getText().toString();

                if(finalIsUpdate){
                    //iya k butang diri kay updatetask na method sa sqlite 16:28
                }
                else{

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
