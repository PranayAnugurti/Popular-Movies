package com.example.pranaykumar.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;

/**
 * Created by PRANAYKUMAR on 11-06-2017.
 * Updated to AndroidX.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
  Context context;
  private ArrayList<Movie> mMoviesData;

  public MoviesAdapter(ArrayList<Movie> moviesdata) {
    mMoviesData = moviesdata;
  }

  @Override
  public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForGridItem = R.layout.grid_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(layoutIdForGridItem, parent, false);
    return new MoviesAdapterViewHolder(view);
  }

  public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {
    public final ImageView mPoster;
    final FrameLayout mLayout;
    public final TextView mMovieName;

    public MoviesAdapterViewHolder(View itemView) {
      super(itemView);
      mPoster = (ImageView) itemView.findViewById(R.id.poster);
      mLayout = (FrameLayout) itemView.findViewById(R.id.layout);
      mMovieName = (TextView) itemView.findViewById(R.id.movieNameTextView);
    }
  }

  @Override
  public void onBindViewHolder(final MoviesAdapterViewHolder holder, final int position) {
    Movie currentMovie = mMoviesData.get(position);
    String basePosterUrl = "https://image.tmdb.org/t/p/w500/";
    String finalPosterUrl = basePosterUrl + currentMovie.getmImageResourceID();
    Picasso.get().load(finalPosterUrl).into(holder.mPoster);
    holder.mMovieName.setText(currentMovie.getmMovieTitle());
    holder.mLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra("movie", (android.os.Parcelable) mMoviesData.get(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            (Activity) v.getContext(), v.findViewById(R.id.poster), "albumArt"
        );
        context.startActivity(intent, options.toBundle());
      }
    });
  }

  @Override
  public int getItemCount() {
    if (null == mMoviesData) return 0;
    return mMoviesData.size();
  }

  public void setmMoviesData(ArrayList<Movie> moviesData) {
    mMoviesData = moviesData;
    notifyDataSetChanged();
  }
}
