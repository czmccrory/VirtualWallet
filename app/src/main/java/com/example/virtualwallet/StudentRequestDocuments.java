package com.example.virtualwallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class StudentRequestDocuments extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    String[] docs = {"Select Document", "Degree Classification"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_request_documents, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        Button request = (Button) view.findViewById(R.id.request);
        Spinner spin = (Spinner) view.findViewById(R.id.docsOptions);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        request.setOnClickListener(this);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.spinner_list, docs);
        aa.setDropDownViewResource(R.layout.spinner_list);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new StudentSendDocuments();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.request:
                Toast.makeText(getContext(), "Sent request", Toast.LENGTH_SHORT).show();
                fragment = new StudentMain();
                loadFragment(fragment);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Choose a document to send.", Toast.LENGTH_SHORT).show();
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