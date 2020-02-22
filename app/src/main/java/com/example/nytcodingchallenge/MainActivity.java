package com.example.nytcodingchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.nytcodingchallenge.apai_connections.ApiCall;
import com.example.nytcodingchallenge.apai_connections.TestApi;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ApiCall apiCall = new ApiCall("https://api.github.com");
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        searchListener();
    }


    private void searchListener() {
        searchButton();
        searchBar();
    }


    private void searchButton(){
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchedOrg = searchBar.getText().toString();
                Log.d("NytApp", searchedOrg);
                makeApiCall();
            }
        });
    }


    private void makeApiCall() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    setResponse(apiCall.getResponse());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void setResponse(String response){
        Log.d("NytApp", "Testing");
        System.out.println(response);
    }

    private void newTest(){
        TestApi test1 = new TestApi();
        String responseString = null;
        try {
            responseString = test1.getResponse();
            Log.d("NytApp", "Test success");
        } catch (IOException e) {
            Log.d("NytApp", "Test failed");
            e.printStackTrace();
        }
        System.out.println(responseString);
    }

    private void searchBar() {
        //TODO Add search bar Async/RxJava to search on change
    }



    private OkHttpClient client = new OkHttpClient();

    private void OkHttpRequests(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
    }
}

