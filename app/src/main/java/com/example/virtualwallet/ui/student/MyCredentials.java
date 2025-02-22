package com.example.virtualwallet.ui.student;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.virtualwallet.Login;
import com.example.virtualwallet.MainActivity;
import com.example.virtualwallet.R;
import com.example.virtualwallet.model.AcceptCredentialResult;
import com.example.virtualwallet.model.Credential;
import com.example.virtualwallet.model.CredentialRequest;
import com.example.virtualwallet.model.CredentialResult;
import com.example.virtualwallet.service.AcceptCredential;
import com.example.virtualwallet.service.ListCredentials;
import com.example.virtualwallet.ui.credentials.CredentialsViewModel;
import com.example.virtualwallet.ui.credentials.OfferCredential;
import com.google.crypto.tink.subtle.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyCredentials extends Fragment implements View.OnClickListener, OfferCredential.OfferDialogListener,
        ListCredentials.ListCredentialsHandler, AcceptCredential.AcceptCredentialHandler{

    private CredentialsViewModel mViewModel;
    private MyCredentials.CredentialArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_credentials, container, false);

        Button back = (Button) view.findViewById(R.id.back);
        Button logout = (Button) view.findViewById(R.id.logout);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);

        final ListView listview = view.findViewById(R.id.credentialListView);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        //Creates a new ArrayList that holds all user credentials
        final List<Credential> list = new ArrayList<>();
        adapter = new MyCredentials.CredentialArrayAdapter(mainActivity,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        //Accepts credential
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                try {
                    Credential cred = mViewModel.getCredentials().getValue().get(position);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;

                    byte[] signature = new byte[0];
                    signature = mainActivity.StudentSign("{}".getBytes(StandardCharsets.UTF_8));

                    AcceptCredential task = new AcceptCredential(
                            MyCredentials.this,
                            mainActivity.getStudentCloudAgentId(),
                            Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
                    );

                    task.execute(cred);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

        });

        mViewModel = new ViewModelProvider(this).get(CredentialsViewModel.class);

        //Lists credential
        final Observer<List<Credential>> credentialObserver = new Observer<List<Credential>>() {
            @Override
            public void onChanged(List<Credential> credentials) {
                MutableLiveData<List<Credential>> creds = mViewModel.getCredentials();
                adapter.clear();
                adapter.addAll(Objects.requireNonNull(creds.getValue()));
            }
        };
        mViewModel.getCredentials().observe(getViewLifecycleOwner(), credentialObserver);

        try {
            getCredentials();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new StudentHome();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
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
     * Displays an option to accept or reject a credential being offered
     * (This is not fully implemented due to problems with layout)
     * @param piid  Piid of credential being issued/offered
     * @param label Label of credential being issued/offered
     */
    public void onOffer(String piid, String label) {
        DialogFragment offerFrag = new OfferCredential(piid, label);
        offerFrag.setTargetFragment(this, 0);
        offerFrag.show(getParentFragmentManager(), "offers");
    }

    /**
     * Displays accepted credential in Credentials list
     * @param piid Piid of credential being issued/offered
     */
    public void accepted(String piid) {
        getCredentials();
    }

    @Override
    public void HandleConnections(AcceptCredentialResult result) {
        getCredentials();
    }

    /**
     * Checks if the list of credentials is empty
     * If not, gets the details of each credential and them saves to
     * their allocated details in the Credential skeleton
     * @param result Result of "List Credentials" API call
     */
    @Override
    public void HandleCredentials(CredentialResult result) {
        if(result != null) {
            mViewModel.getCredentials().setValue(result.credentials);
        }
    }

    @Override
    public void onAcceptCredentialClick(String piid, String label) {}

    @Override
    public void onRejecCredentialClick(DialogFragment dialog) {}

    private static class CredentialArrayAdapter extends ArrayAdapter<Credential> {

        HashMap<Credential, Integer> mIdMap = new HashMap<>();

        //Adds credential to list
        public CredentialArrayAdapter(Context context, int textViewResourceId,
                                      List<Credential> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Credential cred = objects.get(i);
                System.out.println(cred.Comment + " : " + cred.id + " : " + cred.MyDID + " : " + cred.TheirDID);
                mIdMap.put(objects.get(i), i);
            }
        }

        //Gets the item at position clicked
        @Override
        public long getItemId(int position) {
            Credential item = getItem(position);
            Integer pos = mIdMap.get(item);
            if (pos == null) {
                return -1;
            }

            return pos;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    /**
     * Gets credentials from API by calling the "List Credentials" class
     */
    public void getCredentials() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        CredentialRequest req = new CredentialRequest();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(req);

        List<Credential> out = new ArrayList<>();
        try {
            byte[] signature = mainActivity.StudentSign(json.getBytes(StandardCharsets.UTF_8));

            ListCredentials task = new ListCredentials(
                    this,
                    mainActivity.getStudentCloudAgentId(),
                    Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
            );

            task.execute(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}