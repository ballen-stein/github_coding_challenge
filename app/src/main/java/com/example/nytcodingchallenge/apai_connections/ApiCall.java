package com.example.nytcodingchallenge.apai_connections;

import java.io.IOException;

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

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String getUrl(){
        return url;
    }

    public String getResponse() throws IOException {
        return run();
    }
}
