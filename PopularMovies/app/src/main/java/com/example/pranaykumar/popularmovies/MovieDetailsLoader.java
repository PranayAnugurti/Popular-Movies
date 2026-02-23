package com.example.pranaykumar.popularmovies;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import java.util.ArrayList;

/**
 * Created by PRANAYKUMAR on 09-06-2017.
 * Updated to use AndroidX AsyncTaskLoader.
 */

public class MovieDetailsLoader extends AsyncTaskLoader<ArrayList<ArrayList<String>>> {
  private String mVideosURL;
  private String mReviewsURL;

  public MovieDetailsLoader(Context context, String videosURL, String reviewsURL) {
    super(context);
    mVideosURL = videosURL;
    mReviewsURL = reviewsURL;
  }

  @Override
  protected void onStartLoading() {
    forceLoad();
  }

  @Override
  public ArrayList<ArrayList<String>> loadInBackground() {
    if (mReviewsURL == null || mVideosURL == null) {
      return null;
    }
    ArrayList<String> videos = QueryUtils.fetchVideos(mVideosURL);
    ArrayList<String> reviews = QueryUtils.fetchReviews(mReviewsURL);
    ArrayList<ArrayList<String>> details = new ArrayList<>();
    details.add(0, videos);
    details.add(1, reviews);
    return details;
  }
}
