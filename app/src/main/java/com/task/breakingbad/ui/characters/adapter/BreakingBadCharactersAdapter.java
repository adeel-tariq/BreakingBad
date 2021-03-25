package com.task.breakingbad.ui.characters.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class BreakingBadCharactersAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private CharactersItemClickListener mListener;

    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private List<BreakingBadCharactersResponse> mPostItems = new ArrayList<>();
    private Context mContext;

    public BreakingBadCharactersAdapter(Context context, List<BreakingBadCharactersResponse> dataItems, CharactersItemClickListener rentableProductsFragment) {
        mContext = context;
        mListener = rentableProductsFragment;
        this.mPostItems.addAll(dataItems);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_characters_row_layout, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_row_layout, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems == null ? 0 : mPostItems.size();
    }

    public void addItems(List<BreakingBadCharactersResponse> postItems) {
        mPostItems.addAll(postItems);
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new BreakingBadCharactersResponse());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        BreakingBadCharactersResponse item = (BreakingBadCharactersResponse) getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    Object getItem(int position) {
        return mPostItems.get(position);
    }

    class ViewHolder extends BaseViewHolder {

        ItemCharactersRowLayoutBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemCharactersRowLayoutBinding.bind(itemView);
        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);

            if (mPostItems.get(position) != null) {

                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
                circularProgressDrawable.setStrokeWidth(10f);
                circularProgressDrawable.setCenterRadius(80f);
                int color = mContext.getResources().getColor(R.color.colorAccentLight);
                circularProgressDrawable.setColorSchemeColors(
                        color);
                circularProgressDrawable.start();

                Glide.with(mContext).load(mPostItems.get(position).getImg())
                        .placeholder(circularProgressDrawable)
                        .transition(withCrossFade())
                        .error(R.drawable.image_load_failed)
                        .listener(new RequestListener<Drawable>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

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

                mBinding.characterName.setText(mPostItems.get(position).getName());
                mBinding.characterAge.setText(mPostItems.get(position).getBirthday());

                if (mPostItems.get(position).getBirthday() != null && !mPostItems.get(position).getBirthday().isEmpty() && !mPostItems.get(position).getBirthday().equalsIgnoreCase("unknown")) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa");
                    try {
                        Date birthDate = simpleDateFormat.parse(mPostItems.get(position).getBirthday() + " 05:30:00 AM");
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
                    mBinding.characterAge.setText("Birthday is " + mPostItems.get(position).getBirthday());
                }

            }
        }
    }

    public String printDifference(Date startDate, Date endDate) {

        Interval interval =
                new Interval(startDate.getTime(), endDate.getTime());
        Period period = interval.toPeriod();
        return period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days " + period.getHours() + " hrs " + period.getMinutes() + " mins " + period.getSeconds() + " secs";

    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }

    public interface CharactersItemClickListener {
        void onProductClick(View view, String productUuId);
    }
}
