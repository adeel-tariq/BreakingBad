package com.task.breakingbad.network;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import static com.task.breakingbad.network.Status.ERROR;
import static com.task.breakingbad.network.Status.SUCCESS;

// Model class for storing api response either be the correct response or any faced exception
public class ApiResponse<T> {

    // tells the status to be success or error for an API call
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final Throwable error;

    // constructor
    private ApiResponse(Status status, @Nullable T data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    // if api response is success
    public static ApiResponse success(@NonNull Object data) {
        return new ApiResponse(SUCCESS, data, null);
    }

    // if api response is error
    public static ApiResponse error(@NonNull Throwable error) {
        return new ApiResponse(ERROR, null, error);
    }

}
