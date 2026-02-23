package com.example.pranaykumar.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

/**
 * Created by PRANAYKUMAR on 12-06-2017.
 * Updated to AndroidX.
 */

public class FavouriteMoviesAdapter extends
    RecyclerView.Adapter<FavouriteMoviesAdapter.FavouriteMoviesAdapterViewHolder> {

  private final Context mContext;
  private Cursor mCursor;

  public FavouriteMoviesAdapter(Context context) {
    mContext = context;
  }

  @Override
  public FavouriteMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater
        .from(mContext)
        .inflate(R.layout.grid_item, parent, false);
    view.setFocusable(true);
    return new FavouriteMoviesAdapterViewHolder(view);
  }

  @Override
  public void onBindViewHolder(FavouriteMoviesAdapterViewHolder holder, int position) {
    mCursor.moveToPosition(position);
    final Movie currentMovie = new Movie(mCursor.getString(1), mCursor.getString(0),
        mCursor.getString(4), mCursor.getDouble(3), mCursor.getString(2), mCursor.getString(5), 1);
    String posterId = mCursor.getString(0);
    String basePosterUrl = "https://image.tmdb.org/t/p/w185/";
    String finalPosterUrl = basePosterUrl + posterId;
    Picasso.get().load(finalPosterUrl).into(holder.mPosterImageView);
    holder.mMovieName.setText(currentMovie.getmMovieTitle());
    holder.layout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, MovieDetailsActivity.class);
        intent.putExtra("movie", (android.os.Parcelable) currentMovie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            (Activity) v.getContext(), v.findViewById(R.id.poster), "albumArt"
        );
        mContext.startActivity(intent, options.toBundle());
      }
    });
  }

  @Override
  public int getItemCount() {
    if (null == mCursor) {
      return 0;
    }
    return mCursor.getCount();
  }

  void swapCursor(Cursor newCursor) {
    mCursor = newCursor;
    notifyDataSetChanged();
  }

  class FavouriteMoviesAdapterViewHolder extends RecyclerView.ViewHolder {

    final ImageView mPosterImageView;
    final TextView mMovieName;
    private FrameLayout layout;

    public FavouriteMoviesAdapterViewHolder(View itemView) {
      super(itemView);
      mPosterImageView = (ImageView) itemView.findViewById(R.id.poster);
      mMovieName = (TextView) itemView.findViewById(R.id.movieNameTextView);
      layout = (FrameLayout) itemView.findViewById(R.id.layout);
    }
  }
}
