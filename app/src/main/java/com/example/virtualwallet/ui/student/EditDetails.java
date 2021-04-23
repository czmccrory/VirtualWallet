package com.example.virtualwallet.ui.student;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EditDetails extends Fragment implements View.OnClickListener{
    EditText editName, editDob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);
        FloatingActionButton done = (FloatingActionButton) view.findViewById(R.id.doneBtn);
        editName = (EditText) view.findViewById(R.id.editName);
        editDob = (EditText) view.findViewById(R.id.editDob);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        done.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new MyDetails();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.doneBtn:
                try {
                    WriteData(getContext(), editName.getText().toString(), editDob.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    /**
     * Writes to file
     * @param c
     * @param name Name to be written to file
     * @param dob Date of birth to be written to file
     * @throws IOException Error if anything goes wrong
     */
    private void WriteData(Context c, String name, String dob) throws IOException {
        try {
            FileOutputStream fileOut = c.openFileOutput("user_data.txt", c.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(name + ";" + dob + ";");
            outputWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}