package com.example.virtualwallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class StudentNotifications extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_notifications, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        Button NCR = (Button) view.findViewById(R.id.NCR);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        NCR.setOnClickListener(this);

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
            case R.id.NCR:
                fragment = new StudentSendDocuments();
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
        if(fragment.getClass().getName().equals("com.example.virtualwallet.StudentSendDocuments.java"))      {
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
}