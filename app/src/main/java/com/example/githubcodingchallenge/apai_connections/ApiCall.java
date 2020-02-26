package com.example.githubcodingchallenge.apai_connections;

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

    public Response getResponse() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute();
    }

}
