package com.example.nytcodingchallenge.apai_connections;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestApi {

    private OkHttpClient client = new OkHttpClient();
    //String url = "https://raw.github.com/square/okhttp/master/README.md";
    private String url = "https://api.github.com";

    String run() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //responseString = response.body().string();
            return response.body().string();
        }
    }

    public String getResponse() throws IOException {
        //responseString = run();
        //return responseString;
        return run();
    }
}