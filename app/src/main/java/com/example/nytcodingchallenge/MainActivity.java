package com.example.nytcodingchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.nytcodingchallenge.apai_connections.ApiCall;
import com.example.nytcodingchallenge.model.Organization;
import com.example.nytcodingchallenge.model.OrganizationRepoCount;
import com.example.nytcodingchallenge.recycler_view.RepositoryViews;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //private ApiCall apiCall = new ApiCall("https://api.github.com");
    private ApiCall apiCall;
    private String apiUrl, searchedOrg;
    private int public_repos = -1, repoPageCount = 1;
    private Context mContext;

    private EditText searchBar;

    List<Organization> orgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
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
                searchedOrg = searchBar.getText().toString();
                Log.d("GithubChallengeApp", "Searched organization: " + searchedOrg);
                public_repos = -1;
                repoPageCount = 1;
                orgList = new ArrayList<>();

                apiUrl = "https://api.github.com/users/" + searchedOrg;
                makeApiCalls(false);

                //apiUrl += "/repos";
                //apiCall = new ApiCall(apiUrl);
                //makeApiCall(true);

                //apiUrl = "https://api.github.com/users/" + searchedOrg + "/repos?per_page=100;page="+(i);
                //System.out.println("https://api.github.com/users/" + searchedOrg + "/repos?per_page=100;page="+i+1);
                //System.out.println("https://api.github.com/users/" + searchedOrg + "/repos?per_page=100;page="+(i+1));
                //printRepos();
            }
        });
    }

    private void makeApiCalls(boolean getRepoList){
        Observable<Response> observable = Observable.create(new ObservableOnSubscribe<Response>() {
            OkHttpClient client = new OkHttpClient();

            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws Throwable {
                try{
                    Request request = new Request.Builder()
                            .url(apiUrl)
                            .build();
                    System.out.println(apiUrl);

                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){

                        emitter.onNext(response);
                    }
                    emitter.onComplete();
                } catch (IOException e){
                    if(!emitter.isDisposed()){
                        emitter.onError(e);
                    }
                }
            }
        });

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                try {
                    Response response = (Response) o;
                    //makeRealCall(response.body().string());
                    //setResponse(response.body().string());
                    if(!getRepoList){
                        makeRealCall(response.body().string());
                    } else {
                        setResponse(response.body().string());
                        //public_repos = 0;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                if(public_repos > 0){
                    Log.d("GithubChallengeApp", "Starting loop");
                    apiUrl = "https://api.github.com/users/" + searchedOrg + "/repos?per_page=100;page="+(repoPageCount);
                    repoPageCount++;
                    public_repos-=100;
                    apiCall = new ApiCall(apiUrl);
                    makeApiCalls(true);
                } else {
                    //printRepos();
                    setRepos();
                }
            }
        };

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);


    }

    private void setRepos() {
        sortReposByStars();
        startDataDisplay();
        //printRepos();
    }

    private void startDataDisplay() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RepositoryViews repositoryViews = new RepositoryViews(mContext);
                    repositoryViews.setTopRepos();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error with the layout");
        }
    }

    private void makeRealCall(String response) throws IOException {
        //Log.d("GithubChallengeApp", "Response body (String): " + responseBody);
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OrganizationRepoCount> jsonAdapter = moshi.adapter(OrganizationRepoCount.class);
        OrganizationRepoCount repoCount = jsonAdapter.fromJson(response);
        public_repos = repoCount.getPublic_repos();
        Log.d("GithubChallengeApp", "Public Repos count: " + public_repos);
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
        List<Organization> list;

        try {
            list = jsonAdapter.fromJson(response);
            orgList.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed due to: " + e);
        }
    }

    private void sortReposByStars(){
        Comparator<Organization> compareByStars = new Comparator<Organization>() {
            @Override
            public int compare(Organization org1, Organization org2) {
                return Integer.compare(org1.getStargazers_count(),org2.getStargazers_count());
            }
        };
        Collections.sort(orgList, compareByStars.reversed());
    }

    private void printRepos(){
        Log.d("GithubChallengeApp", "List size: " + orgList.size());

        Comparator<Organization> compareByStars = new Comparator<Organization>() {
            @Override
            public int compare(Organization org1, Organization org2) {
                return Integer.compare(org1.getStargazers_count(),org2.getStargazers_count());
            }
        };
        Collections.sort(orgList, compareByStars.reversed());

        ArrayList<Organization> temp = getSortedRepos();

        try {
            for(int i = 0; i < temp.size(); i++){
                Log.d("GithubChallengeApp", i+": " + temp.get(i).getFull_name() + ", " + temp.get(i).getStargazers_count() + ", " + temp.get(i).getHtml_url());
            }
        } catch (Exception e){
            Log.d("GithubChallengeApp", "This organization has less than 3 repos");
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


    public ArrayList<Organization> getSortedRepos(){
        ArrayList<Organization> tempList = new ArrayList<>();
        for(int i=0; i < 3; i++){
            tempList.add(orgList.get(i));
        }
        return tempList;
    }

    private void searchBar() {
        //TODO Add search bar Async/RxJava to search on change
    }

    public void starWebView(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

