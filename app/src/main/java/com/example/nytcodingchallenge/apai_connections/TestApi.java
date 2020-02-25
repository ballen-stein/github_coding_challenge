package com.example.nytcodingchallenge.apai_connections;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;

public class TestApi {

    //private String apiUrl;
    private OkHttpClient client;
    private Request request;

    public TestApi(String url){
        //apiUrl = url;
        client = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();
    }

    Observable<Response> observer = (Observable<Response>) Observable.create(new ObservableOnSubscribe<Response>() {

        @Override
        public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws Throwable {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    emitter.onNext(response);
                }
                emitter.onComplete();
            } catch (IOException e){
                    emitter.onError(e);
            }
        }
    }).subscribeOn(Schedulers.io())
            .observeOn(mainThread())
            .subscribe();

}