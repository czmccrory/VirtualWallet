package com.example.virtualwallet.ui.connections;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualwallet.model.Connection;

import java.util.List;

/**
 * This class prepares the live data received from the API
 */
public class ConnectionsViewModel extends ViewModel {
    private MutableLiveData<List<Connection>> studentConnections, companyConnections, uniConnections;

    public MutableLiveData<List<Connection>> getStudentConnections() {
        if (studentConnections == null) {
            studentConnections = new MutableLiveData<>();
        }
        return studentConnections;
    }

    public MutableLiveData<List<Connection>> getCompanyConnections() {
        if (companyConnections == null) {
            companyConnections = new MutableLiveData<>();
        }
        return companyConnections;
    }

    public MutableLiveData<List<Connection>> getUniConnections() {
        if (uniConnections == null) {
            uniConnections = new MutableLiveData<>();
        }
        return uniConnections;
    }
}
