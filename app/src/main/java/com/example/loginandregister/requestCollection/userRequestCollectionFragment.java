package com.example.loginandregister.requestCollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.loginandregister.R;

public class userRequestCollectionFragment extends Fragment {

    private EditText user_username_editText;
    private EditText user_userLatitude_editText;
    private EditText user_userLongitude_editText;
    private EditText user_userRequestMessage_editText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment_user_request_collection, container, false);

        displayDataToScreen(view);



        return view;
    }

    private void displayDataToScreen(View view) {
        //this is to display the data to editTexts

        user_username_editText = view.findViewById(R.id.user_username);
        user_userLatitude_editText = view.findViewById(R.id.user_latvalue);
        user_userLongitude_editText = view.findViewById(R.id.user_longvalue);
        user_userRequestMessage_editText = view.findViewById(R.id.user_request_message);

        user_username_editText.setInputType(InputType.TYPE_NULL);
        user_userLatitude_editText.setInputType(InputType.TYPE_NULL);
        user_userLongitude_editText.setInputType(InputType.TYPE_NULL);


        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");
        String user_latitude = user_getUserLatitude();
        String user_longitude = user_getUserLongitude();

        user_username_editText.setText(username);
        user_userLatitude_editText.setText(user_latitude);
        user_userLongitude_editText.setText(user_longitude);

    }

    private String user_getUserLongitude() {
        String user_long_value = "";
        //get CURRENT LOCATION
        //then get only the long values
        //then parse it to String


        return user_long_value;
    }

    private String user_getUserLatitude() {
        String user_lat_value = "";
        //get CURRENT LOCATION
        //then get only the lat values
        //then parse it to String

        return user_lat_value;
    }


}