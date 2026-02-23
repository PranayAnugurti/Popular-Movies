package com.example.pranaykumar.popularmovies;

import static com.example.pranaykumar.popularmovies.R.drawable.red_circle;
import static com.example.pranaykumar.popularmovies.R.drawable.white_circle;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;
import com.example.pranaykumar.popularmovies.data.PopularMoviesContract.FavouriteMoviesEntry;
import com.example.pranaykumar.popularmovies.data.PopularMoviesDbHelper;
import com.example.pranaykumar.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovieDetailsActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<ArrayList<String>>> {

  ActivityMovieDetailsBinding movieDetailsBinding;
  private String moviesDbURL = "https://api.themoviedb.org/3/";
  private String apiKey = "?api_key=857710a9c17b11d80aa32f98d00aa936";
  private static final int LOADER_ID = 1;
  ArrayList<String> sTrailers;
  ArrayList<String> sReviews;
  String videosURL;
  String reviewsURL;
  String movieTitle;
  String posterId;
  String movieOverView;
  String movieRating;
  String movieReleaseDate;
  String id;
  int isFav = 0;
  String finalPosterUrl;
  VideoAdapter adapter;
  Cursor cursor;
  private static final String[] FAV_MOVIE_PROJECTION = {
      FavouriteMoviesEntry.COLUMN_NAME,
  };

  private static String selection;
  private static String[] selectionArgs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_details);
    ActionBar actionBar = getSupportActionBar();
    actionBar.hide();
    movieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
    adapter = new VideoAdapter(this, new ArrayList<Video>());

    movieDetailsBinding.videosList.setAdapter(adapter);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      movieDetailsBinding.videosList.setNumColumns(2);
    } else {
      movieDetailsBinding.videosList.setNumColumns(1);
    }
    movieDetailsBinding.videosList.clearFocus();
    setUpTransitions();

    Intent intent = getIntent();
    Bundle b = intent.getExtras();
    Movie currentMovie = b.getParcelable("movie");

    movieTitle = currentMovie.getmMovieTitle();
    posterId = currentMovie.getmImageResourceID();
    movieOverView = currentMovie.getmOverView();
    movieRating = String.valueOf(currentMovie.getmRating());
    movieReleaseDate = currentMovie.getmDate();
    id = currentMovie.getmId();
    isFav = currentMovie.getmIsFav();
    searchDB.execute();
    searchDBforMovie.execute();
    videosURL = moviesDbURL + "movie/" + id + "/videos" + apiKey;
    reviewsURL = moviesDbURL + "movie/" + id + "/reviews" + apiKey;

    SimpleDateFormat dt1 = new SimpleDateFormat("LLL dd,yyyy");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

    Date date = null;
    try {
      date = dt.parse(movieReleaseDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    String basePosterUrl = "https://image.tmdb.org/t/p/w500/";
    finalPosterUrl = basePosterUrl + posterId;
    Context context = this;

    Picasso
        .get()
        .load(finalPosterUrl)
        .into(movieDetailsBinding.poster, new com.squareup.picasso.Callback() {
          @Override
          public void onSuccess() {
            Bitmap bm = ((BitmapDrawable) movieDetailsBinding.poster.getDrawable()).getBitmap();
            colorizeFromImage(bm);
          }

          @Override
          public void onError(Exception e) {
          }
        });

    movieRating = movieRating + "/10";
    movieDetailsBinding.dateTextView.setText(dt1.format(date));
    movieDetailsBinding.titleTextView.setText(movieTitle);
    movieDetailsBinding.overviewTextView.setText(movieOverView);
    movieDetailsBinding.ratingTextView.setText(movieRating);

    ConnectivityManager connMgr =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      LoaderManager loaderManager = getSupportLoaderManager();
      loaderManager.initLoader(LOADER_ID, null, this);
    }

    movieDetailsBinding.markAsFavButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isFav == 0) {
          String message = movieTitle + getString(R.string.marked_as_fav);
          Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
          movieDetailsBinding.markAsFavButton.setImageResource(R.drawable.ic_favorite_red_a700_36dp);
          movieDetailsBinding.markAsFavButton.setBackground(
              ContextCompat.getDrawable(MovieDetailsActivity.this, white_circle));
          addToDb();
          isFav = 1;
        } else if (isFav == 1) {
          String message = movieTitle + getString(R.string.removed_from_fav);
          Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
          movieDetailsBinding.markAsFavButton.setImageResource(R.drawable.ic_favorite_white_36dp);
          movieDetailsBinding.markAsFavButton.setBackground(
              ContextCompat.getDrawable(MovieDetailsActivity.this, red_circle));
          removeFromFav();
          isFav = 0;
        }
      }
    });
  }

  private void colorizeFromImage(final Bitmap image) {
    Palette palette = Palette.from(image).generate();
    int defaultPanelColor = 0xFF808080;
    movieDetailsBinding.titleTextView.setBackgroundColor(palette.getVibrantColor(defaultPanelColor));
    movieDetailsBinding.panel.setBackgroundColor(palette.getLightVibrantColor(defaultPanelColor));
  }

  private void setUpTransitions() {
    Slide slide = new Slide(Gravity.BOTTOM);
    slide.excludeTarget(android.R.id.statusBarBackground, true);
    getWindow().setEnterTransition(slide);
    getWindow().setSharedElementsUseOverlay(false);
    getWindow().setTransitionBackgroundFadeDuration(2000);
  }

  private void removeFromFav() {
    Uri uri = FavouriteMoviesEntry.CONTENT_URI;
    uri = uri.buildUpon().appendPath(id).build();
    getContentResolver().delete(uri, null, null);
  }

  private void addToDb() {
    ContentValues contentValues = new ContentValues();
    contentValues.put(FavouriteMoviesEntry.COLUMN_NAME, movieTitle);
    contentValues.put(FavouriteMoviesEntry.COLUMN_POSTER, posterId);
    contentValues.put(FavouriteMoviesEntry.COLUMN_DATE, movieReleaseDate);
    contentValues.put(FavouriteMoviesEntry.COLUMN_RATING, movieRating);
    contentValues.put(FavouriteMoviesEntry.COLUMN_OVERVIEW, movieOverView);
    contentValues.put(FavouriteMoviesEntry.COLUMN_MOVIE_ID, id);
    getContentResolver().insert(FavouriteMoviesEntry.CONTENT_URI, contentValues);
  }

  @Override
  public Loader<ArrayList<ArrayList<String>>> onCreateLoader(int id, Bundle args) {
    return new MovieDetailsLoader(this, videosURL, reviewsURL);
  }

  AsyncTask<Void, Void, Cursor> searchDB = new AsyncTask<Void, Void, Cursor>() {
    @Override
    protected Cursor doInBackground(Void... params) {
      selection = FavouriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
      selectionArgs = new String[]{id};
      PopularMoviesDbHelper moviesDbHelper = new PopularMoviesDbHelper(getApplicationContext());
      SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
      cursor = db.query(FavouriteMoviesEntry.TABLE_NAME, FAV_MOVIE_PROJECTION, selection, selectionArgs, null, null, null);
      return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
      if (cursor.getCount() != 0) {
        if (isFav == 1) {
          movieDetailsBinding.markAsFavButton.setImageResource(R.drawable.ic_favorite_red_a700_36dp);
          movieDetailsBinding.markAsFavButton.setBackground(
              ContextCompat.getDrawable(MovieDetailsActivity.this, white_circle));
        }
      }
    }
  };

  AsyncTask<Void, Void, Cursor> searchDBforMovie = new AsyncTask<Void, Void, Cursor>() {
    @Override
    protected Cursor doInBackground(Void... params) {
      selection = FavouriteMoviesEntry.COLUMN_NAME + "=?";
      selectionArgs = new String[]{movieTitle};
      PopularMoviesDbHelper moviesDbHelper = new PopularMoviesDbHelper(getApplicationContext());
      SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
      cursor = db.query(FavouriteMoviesEntry.TABLE_NAME, FAV_MOVIE_PROJECTION, selection, selectionArgs, null, null, null);
      return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
      if (cursor.getCount() != 0) {
        isFav = 1;
        movieDetailsBinding.markAsFavButton.setImageResource(R.drawable.ic_favorite_red_a700_36dp);
        movieDetailsBinding.markAsFavButton.setBackground(
            ContextCompat.getDrawable(MovieDetailsActivity.this, white_circle));
      }
    }
  };

  @Override
  public void onLoadFinished(Loader<ArrayList<ArrayList<String>>> loader,
      ArrayList<ArrayList<String>> data) {
    sTrailers = data.get(0);
    sReviews = data.get(1);
    setUI();
  }

  private void setUI() {
    adapter.clear();
    movieDetailsBinding.reviewsContainer.removeAllViews();

    // Videos: show only first 3, show button for the rest
    ArrayList<Video> allVideos = new ArrayList<>();
    for (String str : sTrailers) {
      String[] parts = str.split("`");
      if (parts.length >= 2) {
        allVideos.add(new Video(parts[0], parts[1]));
      }
    }

    int initialCount = Math.min(3, allVideos.size());
    for (int i = 0; i < initialCount; i++) {
      adapter.add(allVideos.get(i));
    }

    if (allVideos.size() > 3) {
      movieDetailsBinding.viewMoreVideosButton.setVisibility(View.VISIBLE);
      movieDetailsBinding.viewMoreVideosButton.setOnClickListener(v -> {
        for (int i = 3; i < allVideos.size(); i++) {
          adapter.add(allVideos.get(i));
        }
        movieDetailsBinding.viewMoreVideosButton.setVisibility(View.GONE);
      });
    }

    // Reviews: add each as a CardView
    reviewIndex = 0;
    reviewExpanded = new boolean[sReviews.size()];
    if (sReviews.isEmpty()) {
      TextView noReviews = new TextView(this);
      noReviews.setText("No Reviews available");
      noReviews.setPadding(32, 16, 16, 16);
      movieDetailsBinding.reviewsContainer.addView(noReviews);
    } else {
      for (String review : sReviews) {
        addReviewCard(review);
      }
    }
  }

  private boolean[] reviewExpanded;
  private int reviewIndex = 0;

  private void addReviewCard(String reviewText) {
    View card = getLayoutInflater().inflate(R.layout.review_card,
        movieDetailsBinding.reviewsContainer, false);
    TextView reviewTv = card.findViewById(R.id.review_text);
    TextView viewMoreBtn = card.findViewById(R.id.view_more_btn);

    reviewTv.setText(HtmlCompat.fromHtml(reviewText, HtmlCompat.FROM_HTML_MODE_COMPACT));
    viewMoreBtn.setVisibility(View.VISIBLE);

    final int idx = reviewIndex++;
    View.OnClickListener toggleListener = v -> {
      if (!reviewExpanded[idx]) {
        reviewTv.setMaxLines(Integer.MAX_VALUE);
        reviewTv.setEllipsize(null);
        viewMoreBtn.setText("View Less");
        reviewExpanded[idx] = true;
      } else {
        reviewTv.setMaxLines(6);
        reviewTv.setEllipsize(TextUtils.TruncateAt.END);
        viewMoreBtn.setText("View More");
        reviewExpanded[idx] = false;
      }
    };

    viewMoreBtn.setOnClickListener(toggleListener);
    card.setOnClickListener(toggleListener);

    movieDetailsBinding.reviewsContainer.addView(card);
  }

  @Override
  public void onLoaderReset(Loader<ArrayList<ArrayList<String>>> loader) {
    sTrailers = null;
    sReviews = null;
  }
}
