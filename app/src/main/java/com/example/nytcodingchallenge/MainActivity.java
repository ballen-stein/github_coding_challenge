package com.example.nytcodingchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.nytcodingchallenge.apai_connections.ApiCall;
import com.example.nytcodingchallenge.apai_connections.TestApi;
import com.example.nytcodingchallenge.model.Organization;
import com.example.nytcodingchallenge.model.OrganizationRepoCount;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private ApiCall apiCall = new ApiCall("https://api.github.com");
    private ApiCall apiCall;
    private String apiUrl;
    private EditText searchBar;
    private int public_repos = -1;

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
                Log.d("GithubChallengeApp", searchedOrg);

                apiUrl = "https://api.github.com/users/" + searchedOrg;
                apiCall = new ApiCall(apiUrl);
                makeApiCall(false);

                //apiUrl = "https://api.github.com/users/" + searchedOrg + "/repos";
                //apiUrl += "/repos";
                //apiCall = new ApiCall(apiUrl);
                //makeApiCall(true);

            }
        });
    }


    private void makeApiCall(final boolean getListOfRepos) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("GithubChallengeApp", apiUrl);
                if(getListOfRepos){
                    try{
                        Log.d("GithubChallengeApp", "Getting list of public repos...");
                        int repoCount = (public_repos/30)+1;
                        setResponse(apiCall.getResponse());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d("GithubChallengeApp", "Getting repo list count...");
                        setRepoCount(apiCall.getResponse());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }


    private void setResponse(String response){
        System.out.println(response);
        Log.d("GithubChallengeApp", response);

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Organization.class);
        JsonAdapter<List<Organization>> jsonAdapter = moshi.adapter(type);
        List<Organization> orgList = null;

        try {
            orgList = jsonAdapter.fromJson(response);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed due to: " + e);
        }

        for(int i = 0; i < orgList.size(); i++){
            Log.d("GithubChallengeApp", i+": " + orgList.get(i).getFull_name() + ", " + orgList.get(i).getStargazers_count() + ", " + orgList.get(i).getUrl());
        }
    }


    private void setRepoCount(String response) throws IOException {
        System.out.println(response);

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OrganizationRepoCount> jsonAdapter = moshi.adapter(OrganizationRepoCount.class);
        OrganizationRepoCount repoCount = jsonAdapter.fromJson(response);
        public_repos = repoCount.getPublic_repos();
        Log.d("GithubChallengeApp", "Public Repos count: " + public_repos);
    }

    private void newTest(){
        TestApi test1 = new TestApi();
        String responseString = null;
        try {
            responseString = test1.getResponse();
            Log.d("GithubChallengeApp", "Test success");
        } catch (IOException e) {
            Log.d("GithubChallengeApp", "Test failed");
            e.printStackTrace();
        }
        System.out.println(responseString);
    }

    private void searchBar() {
        //TODO Add search bar Async/RxJava to search on change
    }
}

