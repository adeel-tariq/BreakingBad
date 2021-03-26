package com.task.breakingbad.ui.characters;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.stetho.Stetho;
import com.task.breakingbad.R;
import com.task.breakingbad.data.model.breakingBadCharacters.BreakingBadCharactersResponse;
import com.task.breakingbad.databinding.ActivityBreakingBadCharactersBinding;
import com.task.breakingbad.network.Status;
import com.task.breakingbad.ui.characters.adapter.BreakingBadCharactersAdapter;
import com.task.breakingbad.utils.Constants;
import com.task.breakingbad.utils.PaginationListener;
import com.task.breakingbad.utils.Utils;

import java.util.ArrayList;
import java.util.List;

// main activity class for showing list of characters in list/recyclerview
public class BreakingBadCharacterActivity extends AppCompatActivity implements BreakingBadCharactersListener, SwipeRefreshLayout.OnRefreshListener {

    private ActivityBreakingBadCharactersBinding mBinding; // view binding variable
    private BreakingBadCharactersViewModel mViewModel; // viewModel variable

    private int mCharactersOffset = 0; // offset of api characters response

    private boolean isLastPage = false; // if its last page from API so no more api calls be made in pagination
    private boolean isLoading = false; // is api call in progress

    private boolean mFirstLoading = true; // is it the first api call

    private BreakingBadCharactersAdapter mBreakingBadCharactersAdapter; // breaking bad characters adapter

    // default activity onCreate method to initialize ll thins
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // setting the view theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaking_bad_characters);

        Stetho.initializeWithDefaults(this); // initializing facebook stetho debug library

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_breaking_bad_characters); // initialize binding variable
        mViewModel = new ViewModelProvider(this).get(BreakingBadCharactersViewModel.class); // initialize viewModel
        mViewModel.setCallBackListener(this); // setting viewModel callback
        mBinding.setViewModel(mViewModel); // setting viewModel for the view
        mBinding.setLifecycleOwner(this); // setting life cycle for our view

        // initialize all views and variables
        initViews();

        // observing on viewModel for any data callbacks from data source (API_
        observeViewModel(mViewModel);
        getCharacters();
    }

    private void initViews() {

        // setting swipe refresh colors
        mBinding.swipeRefresh.setOnRefreshListener(this);
        int color = getResources().getColor(R.color.colorAccentLight);
        mBinding.swipeRefresh.setColorSchemeColors(
                color);

        List<BreakingBadCharactersResponse> badCharactersResponsesList = new ArrayList<>();

        // initialising adapter for our characters list
        mBreakingBadCharactersAdapter = new BreakingBadCharactersAdapter(this, badCharactersResponsesList);

        // defining number of views in one line on recyclerview list
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        // setting up recyclerview with layoutManager and adapter
        mBinding.recyclerView.setLayoutManager(mLayoutManager);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(mBreakingBadCharactersAdapter);

        // add scroll listener while user reach near bottom than load more will call if conditions meet i.e user already not on last page
        mBinding.recyclerView.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mFirstLoading = false;
                mCharactersOffset = mCharactersOffset + 10;
                getCharacters();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


    }

    // requesting viewModel to fetch more data from api using limit and offset
    private void getCharacters() {
        if (Utils.connectionStatusOk(this))
            mViewModel.getBreakingBadCharacters(Constants.CHARACTERS_LIMIT, mCharactersOffset);
    }

    // Observer to observe view model for any response from server
    private void observeViewModel(final BreakingBadCharactersViewModel viewModel) {
        viewModel.getBreakingBadCharactersLiveData().observe(this, apiResponse -> {
            mBinding.setIsLoading(false);
            if (apiResponse != null) { // if the api response is not null
                if (apiResponse.status == Status.SUCCESS) { // if the api response was a success

                    List<BreakingBadCharactersResponse> breakingBadCharactersResponseList = (List<BreakingBadCharactersResponse>) apiResponse.data;
                    receivedCharacters(breakingBadCharactersResponseList);

                } else if (apiResponse.status == Status.ERROR) { // if the api response was an error
                    String error = Utils.errorType(apiResponse.error); // checking type of error from utility class
                    if (apiResponse.error.fillInStackTrace().toString().contains("JsonSyntaxException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("JSONException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("org.json")) {
                        error = "Server error. Please try again later.";

                    }
                    Utils.info(this, error, 4); // showing user the error
                }
            }
        });
    }

    // loading data received from API into adapter to show user in recycler view list
    private void receivedCharacters(List<BreakingBadCharactersResponse> breakingBadCharactersResponseList) {
        if (breakingBadCharactersResponseList != null) { // if response list is not null
            if (!breakingBadCharactersResponseList.isEmpty()) { // if response list is not empty

                // if refreshing is visible than hide it
                mBinding.swipeRefresh.setRefreshing(false);

                // removing pagintion loader in case its visible
                if (mCharactersOffset != 0) {
                    mBreakingBadCharactersAdapter.removeLoading();
                }

                // adding more items in adapter to shown them in list
                mBreakingBadCharactersAdapter.addItems(breakingBadCharactersResponseList);
                mBreakingBadCharactersAdapter.addLoading(); // add pagination loader as last item
                isLoading = false;

            } else {
                // if api response list is empty means we have reached last page and tell user about it and remove the pagination loader
                isLastPage = true;
                mBreakingBadCharactersAdapter.removeLoading();
                Utils.info(this, "All characters fetched, total: " + mBreakingBadCharactersAdapter.getItemCount(), 1);
            }
        }
    }

    // viewModel tells about beginning API call and show main loader if its first call or swipe refresh call
    @Override
    public void onStarted() {
        mBinding.setIsLoading(mFirstLoading);
    }

    // if pull down to refresh is called than all the variables are set to default value and API call is made
    @Override
    public void onRefresh() {
        mFirstLoading = true;
        isLastPage = false;
        mCharactersOffset = 0;
        mBreakingBadCharactersAdapter.clear();
        getCharacters();
    }
}