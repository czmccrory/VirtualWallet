package com.example.virtualwallet.ui.uni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.R;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class UniNotifications extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uni_notifications, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        Button student1 = (Button) view.findViewById(R.id.student1);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        student1.setOnClickListener(this);

        student1.setText(getDetails(view)[0]);

        return view;
    }


    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new UniMain();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.student1:
                fragment = new UniSendDocuments();
                loadFragment(fragment);
                break;
        }
    }

    /**
     * First checks if fragment to load is 'StudentSendDocuments.java'
     * Replaces current fragment with Login fragment
     * May add current fragment to backstack, depending on what button was clicked
     * @param fragment Fragment to be displayed
     */
    public void loadFragment(Fragment fragment) {
        if(fragment.getClass().getName().equals("com.example.virtualwallet.StudentDetails"))      {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.start, fragment);
            transaction.addToBackStack(getClass().getName());
            transaction.commit();
        } else {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.start, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
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