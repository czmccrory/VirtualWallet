package com.example.virtualwallet.service;

import android.os.AsyncTask;

import com.example.virtualwallet.model.CloudAgent;
import com.example.virtualwallet.model.Registration;
import com.example.virtualwallet.service.ApiCall;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AsyncTask<Registration, Void, CloudAgent> {

    public interface RegisterHandler {
        void HandleCloudAgent(CloudAgent cloudAgents);
    }

    private final RegisterHandler handler;

    public RegisterActivity(RegisterHandler handler) {
        this.handler = handler;
    }

    @Override
    protected CloudAgent doInBackground(Registration... registrations) {
        //Generates the Register Interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiCall service = retrofit.create(ApiCall.class);

        //Http request to the remote webserver
        Call<CloudAgent> call = service.Register(
                registrations[0]
        );

        try {
            Response<CloudAgent> resp = call.execute();
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
    protected void onPostExecute(CloudAgent cloudAgent) {
        super.onPostExecute(cloudAgent);

        ReentrantLock lock = new ReentrantLock();

        lock.lock();
        try {
            handler.HandleCloudAgent(cloudAgent);
        } finally {
            lock.unlock();
        }
    }
}