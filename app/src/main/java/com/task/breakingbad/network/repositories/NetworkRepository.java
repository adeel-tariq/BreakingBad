package com.task.breakingbad.network.repositories;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.task.breakingbad.utils.Constants;

import java.net.NetworkInterface;

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
        builder.addNetworkInterceptor(new StethoInterceptor());
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mNetworkInterface = retrofit.create(NetworkInterface.class);
    }

    public synchronized static NetworkRepository getInstance() {
        if (mNetworkRepository == null) {
            mNetworkRepository = new NetworkRepository();
        }
        return mNetworkRepository;
    }

}
