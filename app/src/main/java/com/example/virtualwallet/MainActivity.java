package com.example.virtualwallet;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity implements RegisterActivity.RegisterHandler {
    boolean isEmpty;
    private Ed25519Sign.KeyPair signKeyPair;
    private Ed25519Sign.KeyPair nextKeyPair;
    private String CloudAgentId;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checks if file is empty
        CheckData(this);
        //If file is empty
        if(isEmpty) {
            try {
                //Set user's name and date of birth
                WriteData(this, "John Doe", "23 April 1997");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Fragment fragment = new Login();
        loadFragment(fragment);

        try {
            this.signKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signPubKeyBytes = signKeyPair.getPublicKey();

            this.nextKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextPubKeyBytes = nextKeyPair.getPublicKey();

            String encodedSignKey = Base64.encodeToString(signPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            String encodedNextKey = Base64.encodeToString(nextPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            SaveData(getApplicationContext(), encodedSignKey, encodedNextKey);

            Registration reg = new Registration(
                    encodedSignKey,
                    encodedNextKey,
                    "ArwXoACJgOleVZ2PY7kXn7rA0II0mHYDhc6WrBH8fDAc"
            );

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            String json = gson.toJson(reg);

            RegisterActivity task = new RegisterActivity(this, getApplicationContext());
            task.execute(reg);

            GetConnections getConnections = new GetConnections(getApplicationContext());
            getConnections.execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces current fragment with Login fragment
     * @param fragment Fragment to be displayed
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.start, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void HandleCloudAgent(CloudAgent cloudAgent) {
        try{
            this.CloudAgentId = cloudAgent.cloudAgentId;
        }catch (NullPointerException npe) {
            //Do nothing
        }
    }

    /**
     * Writes to file
     * @param view MainActivity interface
     * @param name Name to be written to file
     * @param dob Date of birth to be written to file
     * @throws IOException Error if anything goes wrong
     */
    private void WriteData(MainActivity view, String name, String dob) throws IOException {
        try {
            FileOutputStream fileOut = view.openFileOutput("user_data.txt", view.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(name + ";" + dob + ";");
            outputWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if file is empty
     * @param view MainActivity interface
     */
    public void CheckData(MainActivity view) {
        try {
            FileInputStream fileIn = view.openFileInput("user_data.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            String[] strArray = new String[100];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                strArray = readString.split(";");
            }
            InputRead.close();
            if(!strArray[0].equals("")){
                isEmpty = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isEmpty = true;
        }
    }

    /**
     * Writes to file
     * @param c Current context
     * @param signKey Signing key
     * @param nextKey Next key in chain
     * @throws IOException Error if anything goes wrong
     */
    private void SaveData(Context c, String signKey, String nextKey) throws IOException {
        try {
            FileOutputStream fileOut = c.openFileOutput("data.txt", c.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(signKey + ";" + nextKey + ";");
            outputWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}


