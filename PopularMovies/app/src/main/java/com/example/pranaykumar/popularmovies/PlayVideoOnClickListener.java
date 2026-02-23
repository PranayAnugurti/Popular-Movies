package com.example.pranaykumar.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by PRANAYKUMAR on 09-06-2017.
 * Updated: replaced deprecated YouTube Standalone Player with a plain Intent.
 */

public class PlayVideoOnClickListener implements OnClickListener {

  public PlayVideoOnClickListener(MovieDetailsActivity movieDetailsActivity) {
  }

  @Override
  public void onClick(View v) {
    String videoId = v.getTag().toString();
    Uri youtubeUri = Uri.parse("https://www.youtube.com/watch?v=" + videoId);
    Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
    v.getContext().startActivity(intent);
  }
}
