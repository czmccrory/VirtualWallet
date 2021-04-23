package com.example.virtualwallet.ui.company;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.R;

public class CompanyHome extends Fragment implements  View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_home, container, false);

        Button logout = (Button) view.findViewById(R.id.logout);
        Button companyNotifications = (Button) view.findViewById(R.id.companyNotifications);
        Button companyConnections = (Button) view.findViewById(R.id.companyConnections);

        logout.setOnClickListener(this);
        companyNotifications.setOnClickListener(this);
        companyConnections.setOnClickListener(this);

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
            case R.id.companyNotifications:
                fragment = new CompanyNotifications();
                loadFragment(fragment);
                break;
            case R.id.companyConnections:
                fragment = new CompanyConnections();
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