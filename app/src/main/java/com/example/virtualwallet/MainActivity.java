package com.example.virtualwallet;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.virtualwallet.model.CloudAgent;
import com.example.virtualwallet.model.Registration;
import com.example.virtualwallet.model.Wallet;
import com.example.virtualwallet.service.RegisterActivity;
import com.example.virtualwallet.ui.connections.ScanInvitation;
import com.google.crypto.tink.signature.SignatureConfig;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity implements RegisterActivity.RegisterHandler {
    boolean isEmpty;
    private Ed25519Sign.KeyPair signKeyPair;
    private Ed25519Sign.KeyPair nextKeyPair;
    private String CloudAgentId;
    private static String WALLET_FILE_NAME = "wallet.json";

    ScanInvitation.ScanInvitationListener scanInvitationListener;

    private Wallet wallet;

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

        this.wallet = new Wallet();

        try {
            InputStream in = this.openFileInput(WALLET_FILE_NAME);
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();

            this.wallet = gson.fromJson(new InputStreamReader(in), Wallet.class);

        } catch (FileNotFoundException e) {
            System.out.println("*****************************************************************");
            System.out.println("exception finding file, initializing cloud agent");
            System.out.println("*****************************************************************");
            initializeCloudAgent();
        }

    }

    private void initializeCloudAgent() {
        try {
            SignatureConfig.register();


            Ed25519Sign.KeyPair signKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signPubKeyBytes = signKeyPair.getPublicKey();
            byte[] signPrivKeyBytes = signKeyPair.getPrivateKey();

            Ed25519Sign.KeyPair nextKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextPubKeyBytes = nextKeyPair.getPublicKey();
            byte[] nextPrivKeyBytes = nextKeyPair.getPrivateKey();

            this.wallet.publicSigningKey = Base64.encodeToString(signPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.wallet.publicNextKey = Base64.encodeToString(nextPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            this.wallet.privateSigningKey = Base64.encodeToString(signPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.wallet.privateNextKey = Base64.encodeToString(nextPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Registration reg = new Registration(
                    this.wallet.publicSigningKey,
                    this.wallet.publicNextKey,
                    "ArwXoACJgOleVZ2PY7kXn7rA0II0mHYDhc6WrBH8fDAc"
            );

            RegisterActivity task = new RegisterActivity(this);
            task.execute(reg);

        } catch (GeneralSecurityException e) {
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
        this.wallet.cloudAgentId = cloudAgent.cloudAgentId;

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(this.wallet);

        try {
            OutputStream out = this.openFileOutput(WALLET_FILE_NAME, MODE_APPEND);
            out.write(json.getBytes());
            out.flush();
            out.close();
        }catch(Exception e) {
            e.printStackTrace();
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

    public void setScanInvitationListener(ScanInvitation.ScanInvitationListener scanInvitationListener) {
        this.scanInvitationListener = scanInvitationListener;
    }

    public ScanInvitation.ScanInvitationListener getScanInvitationListener() {
        return scanInvitationListener;
    }

    public byte[] Sign(byte[] data) throws GeneralSecurityException {
        byte[] privKey = Base64.decode(this.wallet.privateSigningKey, Base64.DEFAULT | Base64.NO_WRAP);
        Ed25519Sign signer = new Ed25519Sign(privKey);
        return signer.sign(data);
    }

    public String getCloudAgentId() {
        return this.wallet.cloudAgentId;
    }
}


