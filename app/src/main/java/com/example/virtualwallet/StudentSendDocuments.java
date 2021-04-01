package com.example.virtualwallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StudentSendDocuments extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_send_documents, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        Button selectDoc = (Button) view.findViewById(R.id.selectDoc);
        Button requestDoc = (Button) view.findViewById(R.id.requestDoc);
        Button send = (Button) view.findViewById(R.id.send);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        selectDoc.setOnClickListener(this);
        requestDoc.setOnClickListener(this);
        send.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                FragmentManager manager = getFragmentManager();

                if (manager != null)
                {
                    if(manager.getBackStackEntryCount() >= 1){
                        String topOnStack = manager.getBackStackEntryAt(manager.getBackStackEntryCount()-1).getName();

                        if(topOnStack.equals("com.example.virtualwallet.StudentConnections")) {
                            fragment = new StudentConnections();
                            loadFragment(fragment);
                        } else if(topOnStack.equals("com.example.virtualwallet.StudentNotifications.java")) {
                            fragment = new StudentNotifications();
                            loadFragment(fragment);
                        }
                    }
                }
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.selectDoc:
                //Do something
                break;
            case R.id.requestDoc:
                fragment = new StudentRequestDocuments();
                loadFragment(fragment);
                break;
            case R.id.send:
                Toast.makeText(getContext(), "Document sent.", Toast.LENGTH_SHORT).show();
                fragment = new StudentMain();
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