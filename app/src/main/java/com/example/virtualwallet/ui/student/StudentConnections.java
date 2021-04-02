package com.example.virtualwallet.ui.student;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.virtualwallet.service.AcceptInvitation;
import com.example.virtualwallet.Login;
import com.example.virtualwallet.MainActivity;
import com.example.virtualwallet.R;
import com.example.virtualwallet.model.Connection;
import com.example.virtualwallet.model.ConnectionRequest;
import com.example.virtualwallet.model.ConnectionResult;
import com.example.virtualwallet.model.Invitation;
import com.example.virtualwallet.model.InvitationResult;
import com.example.virtualwallet.service.ListConnections;
import com.example.virtualwallet.ui.connections.AcceptConnection;
import com.example.virtualwallet.ui.connections.ConnectionsViewModel;
import com.example.virtualwallet.ui.connections.ScanInvitation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.crypto.tink.subtle.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StudentConnections extends Fragment implements View.OnClickListener,
        AcceptConnection.AcceptConnectionDialogListener, ScanInvitation.ScanInvitationListener,
        ListConnections.ListConnectionsHandler, AcceptInvitation.AcceptInvitationHandler {

    private String toast;
    ConnectionsViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_student_connections, container, false);

        Button back = (Button) mainView.findViewById(R.id.back);
        Button logout = (Button) mainView.findViewById(R.id.logout);
        FloatingActionButton addConnection = (FloatingActionButton) mainView.findViewById(R.id.addConnection);

        back.setOnClickListener(this);
        logout.setOnClickListener(this);
        addConnection.setOnClickListener(this);

            final ListView listview = mainView.findViewById(R.id.connectionListView);
            MainActivity mainActivity = (MainActivity) getActivity();
            assert mainActivity != null;

            final List<Connection> list = new ArrayList<>();
            final ConnectionArrayAdapter adapter = new ConnectionArrayAdapter(mainActivity,
                    android.R.layout.simple_list_item_1, list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener((parent, view, position, id) -> {});

            mViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);

            final Observer<List<Connection>> connectionObserver = new Observer<List<Connection>>() {
                @Override
                public void onChanged(List<Connection> connections) {
                    MutableLiveData<List<Connection>> conns = mViewModel.getConnections();
                    adapter.clear();
                    adapter.addAll(Objects.requireNonNull(conns.getValue()));
                }
            };
            mViewModel.getConnections().observe(getViewLifecycleOwner(), connectionObserver);

            try {
                getConnections();
            } catch (Exception e) {
                e.printStackTrace();
            }

        mainActivity.setScanInvitationListener(this);
        return mainView;
    }


    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch(v.getId()) {
            case R.id.back:
                fragment = new StudentMain();
                loadFragment(fragment);
                break;
            case R.id.logout:
                fragment = new Login();
                loadFragment(fragment);
                break;
            case R.id.addConnection:
                fragment = new ScanInvitation();
                loadFragment(fragment);
                break;
        }
    }

    /**
     * First checks if fragment to load is 'StudentSendDocuments.java'
     * Replaces current fragment with Login fragment
     * May add current fragment to backstack, depending on what button was clicked
     * @param fragment Fragment to be displayed
     */
    public void loadFragment(Fragment fragment) {
        if(fragment.getClass().getName().equals("com.example.virtualwallet.ui.student.StudentSendDocuments.java"))      {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.start, fragment);
            transaction.addToBackStack(getClass().getName());
            transaction.commit();
        } else {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.start, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void onInvited(String connectionID, String label) {
        DialogFragment connFrag = new AcceptConnection(connectionID, label);
        connFrag.setTargetFragment(this, 0);
        connFrag.show(getParentFragmentManager(), "connections");
    }


    @Override
    public void onAcceptConnectionClick(String connectionID) {

    }

    @Override
    public void onCancelConnectionClick(DialogFragment dialog) {

    }

    @Override
    public void HandleConnections(InvitationResult result) {
        System.out.println("*********************************************");
        System.out.println("Accepted invitation");
        System.out.println("*********************************************");
        this.getConnections();
    }

    @Override
    public void HandleConnections(ConnectionResult connections) {
        if (connections != null && connections.count > 0) {
            mViewModel.getConnections().setValue(connections.connections);
        }
    }

    @Override
    public void onScanSuccess(String invitation) {
        try {
            MainActivity mainActivity = (MainActivity) getActivity();
            assert mainActivity != null;

            Invitation req = new Invitation();
            req.invitation = invitation;
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();
            String json = gson.toJson(req);

            byte[] signature = mainActivity.Sign(json.getBytes(StandardCharsets.UTF_8));

            AcceptInvitation task = new AcceptInvitation(
                    this,
                    mainActivity.getCloudAgentId(),
                    Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
            );

            task.execute(req);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionArrayAdapter extends ArrayAdapter<Connection> {

        HashMap<Connection, Integer> mIdMap = new HashMap<>();

        public ConnectionArrayAdapter(Context context, int textViewResourceId,
                                      List<Connection> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Connection conn = objects.get(i);
                mIdMap.put(conn, i);
            }
        }

        @Override
        public long getItemId(int position) {
            Connection item = getItem(position);
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

    public void getConnections() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        ConnectionRequest req = new ConnectionRequest();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(req);

        List<Connection> out = new ArrayList<>();
        try {
            byte[] signature = mainActivity.Sign(json.getBytes(StandardCharsets.UTF_8));

            ListConnections task = new ListConnections(
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