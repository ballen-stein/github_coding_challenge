package com.example.nytcodingchallenge.apai_connections;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCall {
    private String url;

    public ApiCall(String givenUrl){
        url = givenUrl;
    }

    private OkHttpClient client = new OkHttpClient();

    private String run() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        /*try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }*/
        final String[] apiResponse = new String[1];

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code: " + response);
                } else {
                    apiResponse[0] = response.body().toString();
                }
            }
        });
        return apiResponse[0];
    }

    public String getUrl(){
        return url;
    }

    public String getResponse() throws IOException {
        return run();
    }
}
