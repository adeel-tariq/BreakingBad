package com.task.breakingbad.network;

import com.task.breakingbad.data.model.breakingBadCharacters.BreakingBadCharactersResponse;
import com.task.breakingbad.utils.Constants;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Retrofit api request methods class which points to all api endpoints
public interface NetworkInterface {

    @GET(Constants.CHARACTERS)
    Observable<List<BreakingBadCharactersResponse>> getCharacters(@Query("limit") int limit, @Query("offset") int offset);
}
