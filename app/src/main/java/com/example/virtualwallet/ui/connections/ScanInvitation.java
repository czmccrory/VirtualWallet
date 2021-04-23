package com.example.virtualwallet.ui.connections;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.virtualwallet.ui.company.CompanyConnections;
import com.example.virtualwallet.ui.student.StudentConnections;

import com.example.virtualwallet.MainActivity;
import com.example.virtualwallet.R;
import com.example.virtualwallet.ui.uni.UniConnections;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanInvitation extends Fragment {

    private ScanConnectionViewModel mViewModel;
    private String qrCode;
    private ScanInvitationListener listener;

    public interface ScanInvitationListener {
        void onScanSuccess(String invitation);
    }

    public void setScanInvitationListener(ScanInvitationListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_scan_connection, container, false);

        //Launches intent with customised options
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        integrator.initiateScan();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned : " + result.getContents(), Toast.LENGTH_LONG).show();
                qrCode = result.getContents();
                if (listener != null) {
                    FragmentManager manager = getFragmentManager();
                    Fragment fragment;

                    if (manager != null)
                    {
                        if(manager.getBackStackEntryCount() >= 1){
                            String topOnStack = manager.getBackStackEntryAt(manager.getBackStackEntryCount()-1).getName();

                            if(topOnStack.equals("com.example.virtualwallet.ui.student.StudentConnections")) {
                                listener.onScanSuccess(qrCode);
                                fragment = new StudentConnections();
                                loadFragment(fragment);
                            }
                            if(topOnStack.equals("com.example.virtualwallet.ui.company.CompanyConnections")) {
                                listener.onScanSuccess(qrCode);
                                fragment = new CompanyConnections();
                                loadFragment(fragment);
                            }
                            if(topOnStack.equals("com.example.virtualwallet.ui.uni.UniConnections")) {
                                listener.onScanSuccess(qrCode);
                                fragment = new UniConnections();
                                loadFragment(fragment);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), qrCode, Toast.LENGTH_SHORT).show();
                    Log.i(MainActivity.class.getSimpleName(), "QR Code Found: " + qrCode);
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity)context;
        setScanInvitationListener(mainActivity.getScanInvitationListener());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScanConnectionViewModel.class);
    }

    /**
     * Replaces current fragment with fragment based on button clicked
     * @param fragment Fragment to be displayed
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.start, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
