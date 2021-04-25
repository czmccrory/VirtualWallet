package com.example.virtualwallet.service;

import android.os.AsyncTask;

import com.example.virtualwallet.model.Invitation;
import com.example.virtualwallet.model.InvitationResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcceptInvitation extends AsyncTask<Invitation, Void, InvitationResult> {

    public interface AcceptInvitationHandler {
        void HandleConnections(InvitationResult result);
    }

    private final AcceptInvitationHandler handler;

    private String cloudAgentId;
    private String signature;

    public AcceptInvitation(AcceptInvitationHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }

    @Override
    protected InvitationResult doInBackground(Invitation... invites) {
        //Generates the Accept Invitation Interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiCall apiCall = retrofit.create(ApiCall.class);

        //Http request to the remote webserver
        Call<InvitationResult> call = apiCall.AcceptInvitation(this.cloudAgentId, this.signature, invites[0]);
        try {
            Response<InvitationResult> resp = call.execute();
            if (resp.isSuccessful()) {
                return resp.body();
            } else {
                throw new IOException(resp.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(InvitationResult result) {
        super.onPostExecute(result);

        this.handler.HandleConnections(result);
    }
}
