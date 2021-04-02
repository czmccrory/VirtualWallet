package com.example.virtualwallet.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MyDetails extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_details, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        FloatingActionButton edit = (FloatingActionButton) view.findViewById(R.id.editBtn);
        TextView name = (TextView) view.findViewById(R.id.nameText);
        TextView dob = (TextView) view.findViewById(R.id.dobText);

        name.setText(getDetails(view)[0]);
        dob.setText(getDetails(view)[1]);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        edit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new StudentMain();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.editBtn:
                fragment = new EditMyDetails();
                loadFragment(fragment);
                break;
        }
    }

    /**
     * Replaces current fragment with Login fragment
     * @param fragment Fragment to be displayed
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.start, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Gets details of user from file
     * @param view current view
     * @return Array of details (name and date of birth)
     */
    public String[] getDetails(View view) {
        try {
            FileInputStream fileIn = view.getContext().openFileInput("user_data.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            String[] strArray = new String[100];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                strArray = readstring.split(";");
            }
            InputRead.close();
            return strArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}