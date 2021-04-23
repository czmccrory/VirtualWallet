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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity implements RegisterActivity.RegisterHandler {
    boolean isEmpty;
    private static String STUDENT_WALLET_FILE_NAME = "studentWallet.json";
    private static String COMPANY_WALLET_FILE_NAME = "companyWallet.json";
    private static String UNI_WALLET_FILE_NAME = "uniWallet.json";

    ScanInvitation.ScanInvitationListener scanInvitationListener;

    private Wallet studentWallet, companyWallet, uniWallet;

    ArrayList<CloudAgent> cloudAgents = new ArrayList<CloudAgent>();

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

        this.studentWallet = new Wallet();
        this.companyWallet = new Wallet();
        this.uniWallet = new Wallet();

        try {
            InputStream in1 = this.openFileInput(STUDENT_WALLET_FILE_NAME);
            InputStream in2 = this.openFileInput(COMPANY_WALLET_FILE_NAME);
            InputStream in3 = this.openFileInput(UNI_WALLET_FILE_NAME);
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();

            this.studentWallet = gson.fromJson(new InputStreamReader(in1), Wallet.class);
            this.companyWallet = gson.fromJson(new InputStreamReader(in2), Wallet.class);
            this.uniWallet = gson.fromJson(new InputStreamReader(in3), Wallet.class);
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

            Ed25519Sign.KeyPair signStudentKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signStudentPubKeyBytes = signStudentKeyPair.getPublicKey();
            byte[] signStudentPrivKeyBytes = signStudentKeyPair.getPrivateKey();

            Ed25519Sign.KeyPair nextStudentKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextStudentPubKeyBytes = nextStudentKeyPair.getPublicKey();
            byte[] nextStudentPrivKeyBytes = nextStudentKeyPair.getPrivateKey();

            this.studentWallet.publicSigningKey = Base64.encodeToString(signStudentPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.studentWallet.publicNextKey = Base64.encodeToString(nextStudentPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.studentWallet.privateSigningKey = Base64.encodeToString(signStudentPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.studentWallet.privateNextKey = Base64.encodeToString(nextStudentPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Ed25519Sign.KeyPair signCompanyKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signCompanyPubKeyBytes = signCompanyKeyPair.getPublicKey();
            byte[] signCompanyPrivKeyBytes = signCompanyKeyPair.getPrivateKey();

            Ed25519Sign.KeyPair nextCompanyKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextCompanyPubKeyBytes = nextCompanyKeyPair.getPublicKey();
            byte[] nextCompanyPrivKeyBytes = nextCompanyKeyPair.getPrivateKey();

            this.companyWallet.publicSigningKey = Base64.encodeToString(signCompanyPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.companyWallet.publicNextKey = Base64.encodeToString(nextCompanyPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.companyWallet.privateSigningKey = Base64.encodeToString(signCompanyPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.companyWallet.privateNextKey = Base64.encodeToString(nextCompanyPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Ed25519Sign.KeyPair signUniKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signUniPubKeyBytes = signUniKeyPair.getPublicKey();
            byte[] signUniPrivKeyBytes = signUniKeyPair.getPrivateKey();

            Ed25519Sign.KeyPair nextUniKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextUniPubKeyBytes = nextUniKeyPair.getPublicKey();
            byte[] nextUniPrivKeyBytes = nextUniKeyPair.getPrivateKey();

            this.uniWallet.publicSigningKey = Base64.encodeToString(signUniPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.uniWallet.publicNextKey = Base64.encodeToString(nextUniPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.uniWallet.privateSigningKey = Base64.encodeToString(signUniPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.uniWallet.privateNextKey = Base64.encodeToString(nextUniPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Wallet[] wallets = {studentWallet, companyWallet, uniWallet};
            Register(wallets);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    ReentrantLock lock = new ReentrantLock();

    void Register(Wallet[] wallets) {
        for (int i = 0; i < wallets.length; i++) {
            lock.lock();
            try {
                Registration reg = new Registration(
                        wallets[i].publicSigningKey,
                        wallets[i].publicNextKey,
                        "ArwXoACJgOleVZ2PY7kXn7rA0II0mHYDhc6WrBH8fDAc"
                );

                RegisterActivity task = new RegisterActivity(this);
                task.execute(reg);
            } finally{
                lock.unlock();
            }
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

    @Override
    public void HandleCloudAgent(CloudAgent cloudAgent) {
        for(int x=0; x<3; x++) {
            cloudAgents.add(cloudAgent);
        }

        Set<CloudAgent> set = new HashSet<>(cloudAgents);

        if(set.size() == 3) {
            cloudAgents.clear();
            cloudAgents.addAll(set);

            this.studentWallet.cloudAgentId = cloudAgents.get(0).cloudAgentId;
            this.companyWallet.cloudAgentId = cloudAgents.get(1).cloudAgentId;
            this.uniWallet.cloudAgentId = cloudAgents.get(2).cloudAgentId;

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();
            String json1 = gson.toJson(this.studentWallet);
            String json2 = gson.toJson(this.companyWallet);
            String json3 = gson.toJson(this.uniWallet);

            try {
                OutputStream out1 = this.openFileOutput(STUDENT_WALLET_FILE_NAME, MODE_APPEND);
                out1.write(json1.getBytes());
                out1.flush();
                out1.close();

                OutputStream out2 = this.openFileOutput(COMPANY_WALLET_FILE_NAME, MODE_APPEND);
                out2.write(json2.getBytes());
                out2.flush();
                out2.close();

                OutputStream out3 = this.openFileOutput(UNI_WALLET_FILE_NAME, MODE_APPEND);
                out3.write(json3.getBytes());
                out3.flush();
                out3.close();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setScanInvitationListener(ScanInvitation.ScanInvitationListener scanInvitationListener) {
        this.scanInvitationListener = scanInvitationListener;
    }

    public ScanInvitation.ScanInvitationListener getScanInvitationListener() {
        return scanInvitationListener;
    }

    public byte[] StudentSign(byte[] data) throws GeneralSecurityException {
        byte[] privKey = Base64.decode(this.studentWallet.privateSigningKey, Base64.DEFAULT | Base64.NO_WRAP);
        Ed25519Sign signer = new Ed25519Sign(privKey);
        return signer.sign(data);
    }

    public byte[] CompanySign(byte[] data) throws GeneralSecurityException {
        byte[] privKey = Base64.decode(this.companyWallet.privateSigningKey, Base64.DEFAULT | Base64.NO_WRAP);
        Ed25519Sign signer = new Ed25519Sign(privKey);
        return signer.sign(data);
    }

    public byte[] UniSign(byte[] data) throws GeneralSecurityException {
        byte[] privKey = Base64.decode(this.uniWallet.privateSigningKey, Base64.DEFAULT | Base64.NO_WRAP);
        Ed25519Sign signer = new Ed25519Sign(privKey);
        return signer.sign(data);
    }

    public String getStudentCloudAgentId() {
        return this.studentWallet.cloudAgentId;
    }

    public String getCompanyCloudAgentId() {
        return this.companyWallet.cloudAgentId;
    }

    public String getUniCloudAgentId() {
        return this.uniWallet.cloudAgentId;
    }
}


