package com.example.virtualwallet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AsyncTask<Registration, Void, CloudAgent> {

    public interface RegisterHandler {
        void HandleCloudAgent(CloudAgent cloudAgent);
    }

    private final RegisterHandler handler;
    protected Context context;

    public RegisterActivity(RegisterHandler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    @Override
    protected CloudAgent doInBackground(Registration... registrations) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register service = retrofit.create(Register.class);

        Call<CloudAgent> call = service.Register(
                registrations[0]
        );

        try {
            Response<CloudAgent> resp = call.execute();
            if (resp.isSuccessful()) {
                SaveData(context, resp.body().cloudAgentId);
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
        handler.HandleCloudAgent(cloudAgent);
    }

    /**
     * Writes to file
     * @param c Current context
     * @param cloudAgentID Cloud Agent ID
     * @throws IOException Error if anything goes wrong
     */
    private void SaveData(Context c, String cloudAgentID) throws IOException {
        try {
            FileOutputStream fileOut = c.openFileOutput("data.txt", c.MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(cloudAgentID + ";");
            outputWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}