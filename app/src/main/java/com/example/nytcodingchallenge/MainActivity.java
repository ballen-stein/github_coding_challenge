package com.example.nytcodingchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String apiUrl, searchedOrg, githubUrl = "https://api.github.com/orgs/";
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
    }


    private void searchButton(){
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        Observable<Response> observable = Observable.create(new ObservableOnSubscribe<Response>() {
            //OkHttpClient client = new OkHttpClient();

            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws Throwable {
                try{
                    /*Request request = new Request.Builder()
                            .url(apiUrl)
                            .build();

                    Response response = client.newCall(request).execute();*/
                    Response response = new ApiCall(apiUrl).getResponse();
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
        observable.subscribeOn(Schedulers.io())
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
                    RepositoryViews repositoryViews = new RepositoryViews(mContext);
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
            int listTopThree =3;
            if(orgList.size() < 3 && orgList.size() > 0){
                listTopThree = public_repos;
            }
            for(int i=0; i < listTopThree; i++){
                tempList.add(orgList.get(i));
            }
            findViewById(R.id.noReposFoundFrame).setVisibility(View.GONE);
        } catch (Exception e){
            findViewById(R.id.noReposFoundFrame).setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
        return tempList;
    }


    public void starWebView(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

