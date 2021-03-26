package com.task.breakingbad.ui.characters.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.task.breakingbad.R;
import com.task.breakingbad.data.model.breakingBadCharacters.BreakingBadCharactersResponse;
import com.task.breakingbad.databinding.ItemCharactersRowLayoutBinding;
import com.task.breakingbad.utils.BaseViewHolder;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// adapter class for showing list of data in recyclerview
public class BreakingBadCharactersAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_LOADING = 0; // loader should be shown for pagination
    public static final int VIEW_TYPE_NORMAL = 1; // character should be shown in list
    private boolean isLoaderVisible = false; // is the pagination loader visible
    private List<BreakingBadCharactersResponse> mBreakingBadCharactersResponsesList = new ArrayList<>(); // storing all characters data
    private Context mContext; // context for loading images using glide

    // constructor for adapter
    public BreakingBadCharactersAdapter(Context context, List<BreakingBadCharactersResponse> dataItems) {
        mContext = context;
        this.mBreakingBadCharactersResponsesList.addAll(dataItems);
    }

    // default adapter method to create view
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            // should the normal character view cell be drawn/shown
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_characters_row_layout, parent, false));
            // should the loader view cell be drawn/shown
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_row_layout, parent, false));
            default:
                return null;
        }
    }

    // bind the view with holder
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    // get type of view either loader or character view cell
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mBreakingBadCharactersResponsesList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    // get total item count in adater list
    @Override
    public int getItemCount() {
        return mBreakingBadCharactersResponsesList == null ? 0 : mBreakingBadCharactersResponsesList.size();
    }

    // add more items in list as result of api call
    public void addItems(List<BreakingBadCharactersResponse> postItems) {
        mBreakingBadCharactersResponsesList.addAll(postItems);
    }

    // add loader as last item in list to indicate some data being loading
    public void addLoading() {
        isLoaderVisible = true;
        mBreakingBadCharactersResponsesList.add(new BreakingBadCharactersResponse());
        notifyItemInserted(mBreakingBadCharactersResponsesList.size() - 1);
    }

    // remove last loader item from list to indicate api call done
    public void removeLoading() {
        isLoaderVisible = false;
        int position = mBreakingBadCharactersResponsesList.size() - 1;
        BreakingBadCharactersResponse item = (BreakingBadCharactersResponse) getItem(position);
        if (item != null) {
            mBreakingBadCharactersResponsesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // clear all the items from adapter list in case of swipe down refresh
    public void clear() {
        mBreakingBadCharactersResponsesList.clear();
        notifyDataSetChanged();
    }

    // get current item from list
    BreakingBadCharactersResponse getItem(int position) {
        return mBreakingBadCharactersResponsesList.get(position);
    }

    // viewholder for character cell
    class ViewHolder extends BaseViewHolder {

        ItemCharactersRowLayoutBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemCharactersRowLayoutBinding.bind(itemView);
        }

        protected void clear() {
        }

        // bind view items and set some data on the views
        public void onBind(int position) {
            super.onBind(position);

            if (mBreakingBadCharactersResponsesList.get(position) != null) {

                // placeholder while image loads
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
                circularProgressDrawable.setStrokeWidth(10f);
                circularProgressDrawable.setCenterRadius(80f);
                int color = mContext.getResources().getColor(R.color.colorAccentLight);
                circularProgressDrawable.setColorSchemeColors(
                        color);
                circularProgressDrawable.start();

                // loading image into imageView using glide
                Glide.with(mContext).load(mBreakingBadCharactersResponsesList.get(position).getImg())
                        .placeholder(circularProgressDrawable) // loader placeholder
                        .transition(withCrossFade()) // animation when image loads
                        .error(R.drawable.image_load_failed) // error image in case glide is unable to load image from url
                        .listener(new RequestListener<Drawable>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                          return false;
                                      }

                                      // listener called when image is loaded into imageView by glide
                                      @Override
                                      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                          // extracting bitmap color using pallet library to make imageView background color based on dominant color in image to make better UI
                                          Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                          //Generate palette object from bitmap
                                          if (bitmap != null && !bitmap.isRecycled()) {
                                              Palette palette = Palette.from(bitmap).generate();
                                              mBinding.characterImage.setBackgroundColor(palette.getDarkMutedColor(mContext.getResources().getColor(R.color.grey)));
                                          }
                                          return false;
                                      }
                                  }
                        ).into(mBinding.characterImage);

                // set character name
                mBinding.characterName.setText(mBreakingBadCharactersResponsesList.get(position).getName());

                // checking if API returned character birthday or not
                if (mBreakingBadCharactersResponsesList.get(position).getBirthday() != null && !mBreakingBadCharactersResponsesList.get(position).getBirthday().isEmpty() && !mBreakingBadCharactersResponsesList.get(position).getBirthday().equalsIgnoreCase("unknown")) {

                    // if character birthday is given by API than calculating its age in years, months, days, hours, minutes seconds and showing it
                    // calculation is done using joda time java library

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa");
                    try {
                        Date birthDate = simpleDateFormat.parse(mBreakingBadCharactersResponsesList.get(position).getBirthday() + " 05:30:00 AM");
                        Date currentDate = Calendar.getInstance().getTime();
                        if (birthDate != null) {
                            mBinding.characterAge.setText(printDifference(birthDate, currentDate));
                        } else {
                            mBinding.characterAge.setText("Error");
                        }
                        Log.i("infoo", "onBind: " + simpleDateFormat.format(birthDate).trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // if no data given by api than default message is shown
                    mBinding.characterAge.setText("Birthday is " + mBreakingBadCharactersResponsesList.get(position).getBirthday());
                }

            }
        }
    }

    // calculating difference in 2 dates using java joda time library and returning a string to show in view text
    public String printDifference(Date startDate, Date endDate) {

        Interval interval =
                new Interval(startDate.getTime(), endDate.getTime());
        Period period = interval.toPeriod();
        return period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days " + period.getHours() + " hrs " + period.getMinutes() + " mins " + period.getSeconds() + " secs";

    }

    // view holder for progress bar
    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
