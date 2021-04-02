package com.example.virtualwallet.service;

import com.example.virtualwallet.model.AcceptCredentialResult;
import com.example.virtualwallet.model.CloudAgent;
import com.example.virtualwallet.model.ConnectionRequest;
import com.example.virtualwallet.model.ConnectionResult;
import com.example.virtualwallet.model.CredentialRequest;
import com.example.virtualwallet.model.CredentialResult;
import com.example.virtualwallet.model.Invitation;
import com.example.virtualwallet.model.InvitationResult;
import com.example.virtualwallet.model.Registration;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiCall {
    @POST("cloudagents")
    Call<CloudAgent> Register(@Body Registration body);

    @POST("cloudagents/connections")
    Call<ConnectionResult> ListConnections(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Body ConnectionRequest request
    );

    @POST("cloudagents/invitation")
    Call<InvitationResult> AcceptInvitation(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Body Invitation invitation
    );

    @POST("cloudagents/credentials")
    Call<CredentialResult> ListCredentials(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Body CredentialRequest request
    );

    @POST("/cloudagents/credentials/{credential_id}")
    Call<AcceptCredentialResult> AcceptCredential(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Path("credential_id") String credentialId,
            @Body HashMap empty
    );

}
