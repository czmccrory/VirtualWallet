package com.example.virtualwallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.security.MessageDigest;

public class Login extends Fragment implements View.OnClickListener{
    EditText uNameEditTxt, pswdEditTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        uNameEditTxt = (EditText) view.findViewById(R.id.editUName);
        pswdEditTxt = (EditText) view.findViewById(R.id.editPswd);
        Button loginBtn = (Button) view.findViewById(R.id.login);

        loginBtn.setOnClickListener(this);

        return view;
    }

    //Following code is from
    //https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
    private static String hash(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onClick(View v) {
        String username = hash(uNameEditTxt.getText().toString());
        String password = hash(pswdEditTxt.getText().toString());
        Fragment fragment;

        //Checks user input
        //Displays a message if incorrect username and/or password was entered
        if(username.equals(hash("JohnDoe123"))) {
            if(password.equals(hash("Student123"))) {
                fragment = new StudentMain();
                loadFragment(fragment);
            } else {
                Toast.makeText(getContext(), "Incorrect username or password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else if(username.equals(hash("jsUoD"))) {
            if(password.equals(hash("Uni123"))) {
                fragment = new UniMain();
                loadFragment(fragment);
            } else {
                Toast.makeText(getContext(), "Incorrect username or password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else if(username.equals(hash("poNCR"))) {
            if(password.equals(hash("NCR123"))) {
                fragment = new CompanyMain();
                loadFragment(fragment);
            } else {
                Toast.makeText(getContext(), "Incorrect username or password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Incorrect username or password. Please try again.", Toast.LENGTH_SHORT).show();
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
