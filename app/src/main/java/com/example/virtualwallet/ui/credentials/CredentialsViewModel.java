package com.example.virtualwallet.ui.credentials;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualwallet.model.Connection;
import com.example.virtualwallet.model.Credential;

import java.util.List;

/**
 * This class prepares the live data received from the API
 */
public class CredentialsViewModel extends ViewModel {

    private MutableLiveData<List<Credential>> credentials;

    public MutableLiveData<List<Credential>> getCredentials() {
        if (credentials == null) {
            credentials = new MutableLiveData<>();
        }
        return credentials;
    }
}