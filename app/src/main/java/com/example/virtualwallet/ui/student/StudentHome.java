package com.example.virtualwallet.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.R;

public class StudentHome extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        Button logout = (Button) view.findViewById(R.id.logout);
        Button studentDocuments = (Button) view.findViewById(R.id.myCredentials);
        Button studentNotifications = (Button) view.findViewById(R.id.studentNotifications);
        Button studentConnections = (Button) view.findViewById(R.id.studentConnections);
        Button myDetails = (Button) view.findViewById(R.id.myDetails);

        logout.setOnClickListener(this);
        studentDocuments.setOnClickListener(this);
        studentNotifications.setOnClickListener(this);
        studentConnections.setOnClickListener(this);
        myDetails.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.myCredentials:
                fragment = new MyCredentials();
                loadFragment(fragment);
                break;
            case R.id.studentNotifications:
                fragment = new StudentNotifications();
                loadFragment(fragment);
                break;
            case R.id.studentConnections:
                fragment = new StudentConnections();
                loadFragment(fragment);
                break;
            case R.id.myDetails:
                fragment = new MyDetails();
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
}