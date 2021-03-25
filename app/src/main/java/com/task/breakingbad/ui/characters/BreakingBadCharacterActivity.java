package com.task.breakingbad.ui.characters;

import android.os.Bundle;
import android.view.View;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BreakingBadCharacterActivity extends AppCompatActivity implements BreakingBadCharactersListener, SwipeRefreshLayout.OnRefreshListener {

    private ActivityBreakingBadCharactersBinding mBinding;
    private BreakingBadCharactersViewModel mViewModel;

    private int mCharactersLimit = Constants.CHARACTERS_LIMIT;
    private int mCharactersOffset = 0;

    private boolean isLastPage = false;
    private boolean isLoading = false;

    private boolean mFirstLoading = true;
    private boolean isLoaderVisible = false;

    private List<BreakingBadCharactersResponse> mBreakingBadCharactersResponseItemList;
    private BreakingBadCharactersAdapter mProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaking_bad_characters);

        Stetho.initializeWithDefaults(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_breaking_bad_characters);
        mViewModel = new ViewModelProvider(this).get(BreakingBadCharactersViewModel.class);
        mViewModel.setCallBackListener(this);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        initViews();

        observeViewModel(mViewModel);
        getCharacters();
    }

    private void initViews() {

        mBinding.swipeRefresh.setOnRefreshListener(this);
        int color = getResources().getColor(R.color.colorAccentLight);
        mBinding.swipeRefresh.setColorSchemeColors(
                color);

        List<BreakingBadCharactersResponse> dataItems = new ArrayList<>();

        mProductsAdapter = new BreakingBadCharactersAdapter(this, dataItems, new BreakingBadCharactersAdapter.CharactersItemClickListener() {
            @Override
            public void onProductClick(View view, String productUuId) {
            }
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (mProductsAdapter.getItemViewType(position) == BreakingBadCharactersAdapter.VIEW_TYPE_LOADING) {
//                    return mLayoutManager.getSpanCount();
//                }
//                return 1;
//            }
//        });

        mBinding.recyclerView.setLayoutManager(mLayoutManager);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(mProductsAdapter);

        /*
         * add scroll listener while user reach in bottom load more will call
         */
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

    private void getCharacters() {
        mViewModel.getBreakingBadCharacters(mCharactersLimit, mCharactersOffset);
    }

    // Observer to observe view model for any response from server
    private void observeViewModel(final BreakingBadCharactersViewModel viewModel) {
        viewModel.getBreakingBadCharactersLiveData().observe(this, apiResponse -> {
            mBinding.setIsLoading(false);
            if (apiResponse != null) {
                if (apiResponse.status == Status.SUCCESS) {

                    List<BreakingBadCharactersResponse> breakingBadCharactersResponseList = (List<BreakingBadCharactersResponse>) apiResponse.data;
//                    Log.i("infoo", "observeViewModel: " + breakingBadCharactersResponseList.toString());
                    receivedCharacters(breakingBadCharactersResponseList);

                } else if (apiResponse.status == Status.ERROR) {
                    apiResponse.error.printStackTrace();
                    String error = Utils.errorType(apiResponse.error);
                    if (apiResponse.error.fillInStackTrace().toString().contains("JsonSyntaxException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("JSONException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("org.json")) {
                        error = "Server error. Please try again later.";

                    }
                    Utils.info(this, error, 4);
                }
            }
        });
    }

    private void receivedCharacters(List<BreakingBadCharactersResponse> breakingBadCharactersResponseList) {
        if (breakingBadCharactersResponseList != null) {
            if (!breakingBadCharactersResponseList.isEmpty()) {

                mBinding.swipeRefresh.setRefreshing(false);

                if (mCharactersOffset != 0) {
                    mProductsAdapter.removeLoading();
                    isLoaderVisible = false;
                }

                mProductsAdapter.addItems(breakingBadCharactersResponseList);
                mProductsAdapter.addLoading();
                isLoaderVisible = true;
                isLoading = false;

            } else {
                isLastPage = true;
                mProductsAdapter.removeLoading();
                Utils.info(this, "All characters fetched, total: " + mProductsAdapter.getItemCount(), 1);
            }
        }
    }

    @Override
    public void onStarted() {
        mBinding.setIsLoading(mFirstLoading);
    }

    @Override
    public void onRefresh() {
        mFirstLoading = true;
        isLastPage = false;
        mCharactersOffset = 0;
        mProductsAdapter.clear();
        getCharacters();
    }
}