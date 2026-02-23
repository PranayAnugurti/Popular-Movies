package com.example.pranaykumar.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by PRANAYKUMAR on 09-07-2017.
 * Updated: replaced deprecated YouTube Standalone Player with a plain Intent.
 */

public class VideoAdapter extends ArrayAdapter<Video> implements View.OnClickListener {

  Context mContext;

  public VideoAdapter(@NonNull Context context, @NonNull ArrayList<Video> objects) {
    super(context, 0, objects);
    mContext = context;
  }

  @Override
  public void onClick(View v) {
    int position = (int) v.getTag();
    Video currentVideo = getItem(position);
    Uri youtubeUri = Uri.parse("https://www.youtube.com/watch?v=" + currentVideo.getmVideoId());
    Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
    v.getContext().startActivity(intent);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View result = convertView;
    if (result == null) {
      LayoutInflater inflater = LayoutInflater.from(getContext());
      result = inflater.inflate(R.layout.video_item, parent, false);
    }
    Video video = getItem(position);
    TextView textView = (TextView) result.findViewById(R.id.VideoName);
    ImageButton imgButton = (ImageButton) result.findViewById(R.id.videoThumbNail);

    textView.setText(video.getmVideoName());
    String imgUrl = "https://img.youtube.com/vi/" + video.getmVideoId() + "/0.jpg";
    Picasso.get().load(imgUrl).into(imgButton);
    textView.setOnClickListener(this);
    imgButton.setOnClickListener(this);
    textView.setTag(position);
    imgButton.setTag(position);
    return result;
  }
}
