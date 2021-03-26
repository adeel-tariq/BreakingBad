package com.task.breakingbad.network.repositories;

import android.annotation.SuppressLint;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.task.breakingbad.data.model.breakingBadCharacters.BreakingBadCharactersResponse;
import com.task.breakingbad.network.NetworkInterface;
import com.task.breakingbad.utils.Constants;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// this class is used for making api calls using retrofit client
public class NetworkRepository {

    private NetworkInterface mNetworkInterface;
    private static NetworkRepository mNetworkRepository;

    // for setting up retrofit object which is used in api calls
    private NetworkRepository() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor()); // debug interceptor to debug API calls using facebok stetho library in conjunction with chrome browser
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL) // base url of our APU
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mNetworkInterface = retrofit.create(NetworkInterface.class);
    }

    // getting instance of retrofit client which will be used in making API call
    public synchronized static NetworkRepository getInstance() {
        if (mNetworkRepository == null) {
            mNetworkRepository = new NetworkRepository();
        }
        return mNetworkRepository;
    }

    // Observable for calling and observing on the response from API
    @SuppressLint("CheckResult")
    public Observable<List<BreakingBadCharactersResponse>> getCharacters(int limit, int offset) {
        return mNetworkInterface.getCharacters(limit, offset);
    }

}
