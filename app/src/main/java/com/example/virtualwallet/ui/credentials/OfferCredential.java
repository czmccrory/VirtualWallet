package com.example.virtualwallet.ui.credentials;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.virtualwallet.R;
import com.example.virtualwallet.databinding.FragmentOfferCredentialBinding;

public class OfferCredential extends DialogFragment {

    public interface OfferDialogListener {
        void onAcceptCredentialClick(String piid, String label);
        void onRejecCredentialClick(DialogFragment dialog);
    }

    OfferDialogListener listener;
    FragmentOfferCredentialBinding binding;
    String piid;
    String hint;

    public OfferCredential(String piid, String hint) {
        this.piid = piid;
        this.hint = hint;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentOfferCredentialBinding.inflate(requireActivity().getLayoutInflater());
        binding.setCredentialHint(hint);

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());

        adBuilder.setView(binding.getRoot());
        adBuilder.setMessage(R.string.accept_credential).setTitle(R.string.title_credentials).setIcon(R.drawable.ic_action_done)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.executePendingBindings();
                        String name = binding.getCredentialName() == null ? hint : binding.getCredentialHint();
                        listener.onAcceptCredentialClick(piid, name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onRejecCredentialClick(OfferCredential.this);
                    }
                });

        return adBuilder.create();
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (OfferCredential.OfferDialogListener) fragment;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(fragment.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}