package com.task.breakingbad.ui.characters;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.task.breakingbad.network.ApiResponse;
import com.task.breakingbad.network.repositories.NetworkRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

// viewModel for breaking bad characters
public class BreakingBadCharactersViewModel extends AndroidViewModel {

    // listener variable to notify view of any changes
    private BreakingBadCharactersListener mAccountCallBack;

    // Live data for storing api response data and sending it back to view
    private MutableLiveData<ApiResponse> mBreakingBadCharactersResponseMutableLiveData;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    // initializing of viewModel
    public BreakingBadCharactersViewModel(@NonNull Application application) {
        super(application);
    }

    // initializing callback listener
    void setCallBackListener(BreakingBadCharactersListener authCallBack) {
        this.mAccountCallBack = authCallBack;
    }

    // For providing live data to view
    public LiveData<ApiResponse> getBreakingBadCharactersLiveData() {
        if (mBreakingBadCharactersResponseMutableLiveData == null) {
            mBreakingBadCharactersResponseMutableLiveData = new MutableLiveData<>();
        }
        return mBreakingBadCharactersResponseMutableLiveData;
    }

    // get breaking bad characters
    @SuppressLint("CheckResult")
    public void getBreakingBadCharacters(int limit, int offset) {
        mAccountCallBack.onStarted();
        mDisposables.add(NetworkRepository.getInstance().getCharacters(limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mBreakingBadCharactersResponseMutableLiveData.setValue(ApiResponse.success(result)),
                        throwable -> mBreakingBadCharactersResponseMutableLiveData.setValue(ApiResponse.error(throwable))
                ));
    }

}
