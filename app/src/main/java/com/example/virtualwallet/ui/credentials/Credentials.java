package com.example.virtualwallet.ui.credentials;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.virtualwallet.MainActivity;
import com.example.virtualwallet.R;
import com.example.virtualwallet.model.AcceptCredentialResult;
import com.example.virtualwallet.model.Credential;
import com.example.virtualwallet.model.CredentialRequest;
import com.example.virtualwallet.model.CredentialResult;
import com.example.virtualwallet.service.AcceptCredential;
import com.example.virtualwallet.service.ListCredentials;
import com.google.crypto.tink.subtle.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Credentials extends Fragment implements OfferCredential.OfferDialogListener,
        ListCredentials.ListCredentialsHandler, AcceptCredential.AcceptCredentialHandler {

    private CredentialsViewModel mViewModel;
    private Credentials.CredentialArrayAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_credentials, container, false);

        final ListView listview = root.findViewById(R.id.credentialListView);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;


        final List<Credential> list = new ArrayList<>();
        adapter = new Credentials.CredentialArrayAdapter(mainActivity,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                try {
                    Credential cred = mViewModel.getCredentials().getValue().get(position);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;

                    byte[] signature = new byte[0];
                    signature = mainActivity.Sign("{}".getBytes(StandardCharsets.UTF_8));

                    AcceptCredential task = new AcceptCredential(
                            Credentials.this,
                            mainActivity.getCloudAgentId(),
                            Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
                    );

                    task.execute(cred);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

        });

        mViewModel = new ViewModelProvider(this).get(CredentialsViewModel.class);

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

        return root;
    }

    public void onOffer(String piid, String label) {
        DialogFragment offerFrag = new OfferCredential(piid, label);
        offerFrag.setTargetFragment(this, 0);
        offerFrag.show(getParentFragmentManager(), "offers");
    }

    public void accepted(String piid) {
        getCredentials();
    }

    @Override
    public void onAcceptCredentialClick(String piid, String label) {
    }

    @Override
    public void onRejecCredentialClick(DialogFragment dialog) {

    }

    @Override
    public void HandleCredentials(CredentialResult result) {
        mViewModel.getCredentials().setValue(result.credentials);
    }

    @Override
    public void HandleConnections(AcceptCredentialResult result) {
        getCredentials();
    }


    private static class CredentialArrayAdapter extends ArrayAdapter<Credential> {

        HashMap<Credential, Integer> mIdMap = new HashMap<>();

        public CredentialArrayAdapter(Context context, int textViewResourceId,
                                      List<Credential> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Credential cred = objects.get(i);
                System.out.println(cred.Comment + " : " + cred.id + " : " + cred.MyDID + " : " + cred.TheirDID);
                mIdMap.put(objects.get(i), i);
            }
        }

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

    public void getCredentials() {

        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        CredentialRequest req = new CredentialRequest();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(req);

        List<Credential> out = new ArrayList<>();
        try {
            byte[] signature = mainActivity.Sign(json.getBytes(StandardCharsets.UTF_8));

            ListCredentials task = new ListCredentials(
                    this,
                    mainActivity.getCloudAgentId(),
                    Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
            );

            task.execute(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}