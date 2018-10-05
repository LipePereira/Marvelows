package com.lipedev.mervelows.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipedev.mervelows.R;
import com.lipedev.mervelows.fragments.AllComicsFragment.OnListFragmentInteractionListener;
import com.lipedev.mervelows.fragments.FavoriteComicsFragment;
import com.lipedev.mervelows.models.Comic;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Comic} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ComicsRecyclerViewAdapter extends RecyclerView.Adapter<ComicsRecyclerViewAdapter.ViewHolder> {

    private final List<Comic> mComics;
    private final OnListFragmentInteractionListener onListFragmentInteractionListener;
    private final FavoriteComicsFragment.OnFavoriteListFragmentInteractionListener onFavoriteListFragmentInteractionListener;

    public ComicsRecyclerViewAdapter(List<Comic> items, OnListFragmentInteractionListener listener) {
        mComics = items;
        onListFragmentInteractionListener = listener;
        onFavoriteListFragmentInteractionListener = null;
    }

    public ComicsRecyclerViewAdapter(List<Comic> comics, FavoriteComicsFragment.OnFavoriteListFragmentInteractionListener listener) {
        mComics = comics;
        onFavoriteListFragmentInteractionListener = listener;
        onListFragmentInteractionListener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_comic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mComic = mComics.get(position);

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.mDateView.setText(format.format(mComics.get(position).getLaunchDate()));
        holder.mTitleView.setText(mComics.get(position).getTitle());

        Log.i("image", mComics.get(position).getImagePath()
                + "/standard_fantastic."
                + mComics.get(position).getImageExtension());

        Picasso
                .get()
                .load(
                    mComics.get(position).getImagePath()
                            + "/standard_fantastic."
                            + mComics.get(position).getImageExtension())
                .fit()
                .centerCrop()
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onListFragmentInteractionListener) {
                    onListFragmentInteractionListener.onListFragmentInteraction(holder.mComic);
                }
                if (null != onFavoriteListFragmentInteractionListener) {
                    onFavoriteListFragmentInteractionListener.onListFragmentInteraction(holder.mComic);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public Comic mComic;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = view.findViewById(R.id.date);
            mTitleView = view.findViewById(R.id.title);
            mImageView = view.findViewById(R.id.item_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
