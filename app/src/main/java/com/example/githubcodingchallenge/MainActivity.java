package com.example.githubcodingchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.githubcodingchallenge.apai_connections.ApiCall;
import com.example.githubcodingchallenge.model.Organization;
import com.example.githubcodingchallenge.model.OrganizationRepoCount;
import com.example.githubcodingchallenge.recycler_view.RepositoryViews;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.operators.observable.ObservableError;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String apiUrl, searchedOrg, githubUrl = "https://api.github.com/orgs/";
    private int public_repos = -1, repoPageCount = 1;
    private Context mContext;
    private EditText searchBar;
    private RepositoryViews repositoryViews;

    List<Organization> orgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        searchBar = findViewById(R.id.searchBar);
        repositoryViews= new RepositoryViews(mContext);
        searchListener();
    }


    private void searchListener() {
        searchButton();
    }


    private void searchButton(){
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.reposUpdateFrame).bringToFront();
                setApiInformation();
            }
        });
    }


    private void setApiInformation(){
        searchedOrg = searchBar.getText().toString();
        public_repos = -1;
        repoPageCount = 1;
        orgList = new ArrayList<>();
        apiUrl = githubUrl + searchedOrg;

        makeApiCalls(false);
    }


    private void makeApiCalls(boolean getRepoList){
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                try {
                    Response response = (Response) o;
                    if(!getRepoList){
                        getPublicRepoCount(Objects.requireNonNull(response.body()).string());
                    } else {
                        parseResponse(Objects.requireNonNull(response.body()).string());
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
                    apiUrl = githubUrl + searchedOrg + "/repos?per_page=100;page="+(repoPageCount);
                    repoPageCount++;
                    public_repos-=100;
                    new ApiCall(apiUrl);
                    makeApiCalls(true);
                } else {
                    setRepos();
                }
            }
        };

        Observable.defer(() -> {
            try{
                Response response = new ApiCall(apiUrl).getResponse();
                return Observable.just(response);
            } catch (IOException e){
                return Observable.error(e);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(observer);
    }


    private void parseResponse(String response){
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Organization.class);
        JsonAdapter<List<Organization>> jsonAdapter = moshi.adapter(type);
        List<Organization> list;
        try {
            list = jsonAdapter.fromJson(response);
            orgList.addAll(Objects.requireNonNull(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getPublicRepoCount(String response) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OrganizationRepoCount> jsonAdapter = moshi.adapter(OrganizationRepoCount.class);
        OrganizationRepoCount repoCount = jsonAdapter.fromJson(response);
        public_repos = Objects.requireNonNull(repoCount).getPublic_repos();
        setUpdateTextVisibility();
    }


    private void setUpdateTextVisibility(){
        try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.noReposFoundText).setVisibility(View.GONE);
                    findViewById(R.id.reposFoundFrame).setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setRepos() {
        sortReposByStars();
        startDataDisplay();
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


    private void startDataDisplay() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.reposFoundFrame).setVisibility(View.GONE);
                    findViewById(R.id.repoRecyclerView).bringToFront();
                    repositoryViews.setTopRepos();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<Organization> getSortedRepos(){
        ArrayList<Organization> tempList = new ArrayList<>();
        try{
            int listTopThree = 3;
            if(orgList.size() < 3 && orgList.size() > 0){
                listTopThree = public_repos;
            }
            for(int i=0; i < listTopThree; i++){
                tempList.add(orgList.get(i));
            }
        } catch (Exception e){
            findViewById(R.id.noReposFoundText).setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
        return tempList;
    }


    public void starWebView(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorGrey))
                .addDefaultShareMenuItem()
                .setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}