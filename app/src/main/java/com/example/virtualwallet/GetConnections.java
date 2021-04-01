package com.example.virtualwallet;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetConnections extends AsyncTask<Void, Void, Connections> {

    private Context context;

    public GetConnections(Context context){
        this.context = context;
    }

    @Override
    protected Connections doInBackground(Void... voids) {
        String signKey = GetData(context)[0];
        String cloudAgentId = GetData(context)[2];

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        // set your desired log level
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        // add your other interceptors …
//        // add logging as last interceptor
//        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient.build())
                .build();

        ListAndAccept service = retrofit.create(ListAndAccept.class);

        Map<String, String> map = new HashMap<>();
        map.put("x-canis-cloud-agent-id", cloudAgentId);
        map.put("x-canis-cloud-agent-signature", signKey);

        Call<Map<String, Connections>> call = service.GetConnections(map);

        try {
            Response<Map<String, Connections>> resp = call.execute();
            if (resp.isSuccessful()) {
                Map<String, Connections> body = resp.body();

//                for(Connections connections : body.values()) {
//                    System.out.println("id: " + connections.getId());
//                    System.out.println("my_did: " + connections.getMy_did());
//                    System.out.println("name: " + connections.getName());
//                    System.out.println("their_did: " + connections.getTheir_did());
//                    System.out.println("count: " + connections.getCount());
//                }

//                List<Connections> connections = new ArrayList<>(body.values());
//
//                System.out.println(connections.size());
//
//                for(int i = 0; i < connections.size(); i++) {
//                    System.out.println("Connections " + connections.get(i).toString());
//                }

            } else {
                throw new IOException(resp.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets saved keys from file
     * @return Array of details (name and date of birth)
     */
    public String[] GetData(Context context) {
        try {
            FileInputStream fileIn = context.openFileInput("data.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[1000];
            String[] strArray = new String[1000];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                strArray = readstring.split(";");
            }
            InputRead.close();
            return strArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
