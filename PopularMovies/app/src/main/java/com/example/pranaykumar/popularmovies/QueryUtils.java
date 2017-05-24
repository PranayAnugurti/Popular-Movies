package com.example.pranaykumar.popularmovies;


import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PRANAYKUMAR on 23-05-2017.
 */

public class QueryUtils {

  //Tag for the log messages
  private static final String LOG_TAG=QueryUtils.class.getSimpleName();
  private QueryUtils(){
  }
  public static ArrayList<Movie> fetchMovies(String requestUrl){
    URL url=createUrl(requestUrl);
    //Perform HTTP request to the URL and receive a JSON response back
    String jsonResponse=null;
    jsonResponse=makeHttpRequest(url);
    //Extract relevant fields from the JSON response and create a list of {@link Movie}s
    ArrayList<Movie>movies=extractFeatureFromJson(jsonResponse);
    //Return the list of {@link Movie}
    return movies;
  }

  private static ArrayList<Movie> extractFeatureFromJson(String jsonResponse) {
    //If the JSON string is empty or null,then return early.
    if(TextUtils.isEmpty(jsonResponse)){
      return null;
    }
    //Create an empty ArrayList that we can start adding movies
    ArrayList<Movie>movies=new ArrayList<>();
    try {
      JSONObject baseJson=new JSONObject(jsonResponse);
      JSONArray results=baseJson.getJSONArray(String.valueOf(R.string.results_query_word));

      for(int i=0;i<results.length();i++){
        JSONObject currentMovie=results.getJSONObject(i);
        String poster_path=currentMovie.getString(String.valueOf(R.string.poster_query_word));
        String overView=currentMovie.getString(String.valueOf(R.string.overview_query_word));
        String title=currentMovie.getString(String.valueOf(R.string.title_query_word));
        Double rating=currentMovie.getDouble(String.valueOf(R.string.rating_query_word));

        String Rdate=currentMovie.getString(String.valueOf(R.string.release_date_query_word));

        Movie movie=new Movie(title,poster_path,overView,rating,Rdate);
        movies.add(movie);
      }

    }
    catch (JSONException e){
      e.printStackTrace();
    }
    return movies;
  }

  private static String makeHttpRequest(URL url) {
    String jsonResponse="";

    //If the URL is null,then return early.
    if(url==null){
      return jsonResponse;
    }
    HttpURLConnection urlConnection=null;
    InputStream inputStream=null;
    try{
      urlConnection=(HttpURLConnection)url.openConnection();
      Log.i(LOG_TAG,String.valueOf(R.string.connection_opened));
      urlConnection.setReadTimeout(10000/*milliseconds*/);
      urlConnection.setConnectTimeout(15000);
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();
      Log.i(LOG_TAG,String.valueOf(R.string.connected));

      //If the request was succesful (response code 200),
      //then read the input stream and parse the response.
      if(urlConnection.getResponseCode()==200){
        inputStream=urlConnection.getInputStream();
        jsonResponse=readFromStream(inputStream);
        Log.i(LOG_TAG,String.valueOf(R.string.responseCode)+urlConnection.getResponseCode());
      }else{
        Log.e(LOG_TAG,String.valueOf(R.string.responseCode)+urlConnection.getResponseCode());
      }
    } catch (IOException e) {
      Log.e(LOG_TAG,String.valueOf(R.string.ERROR_MESSAGE));
    }finally {
      if(urlConnection!=null){
        urlConnection.disconnect();
      }
      if(inputStream!=null){
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return jsonResponse;
  }

  private static String readFromStream(InputStream inputStream) {
    StringBuilder output=new StringBuilder();
    if(inputStream!=null){
      InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
      BufferedReader reader=new BufferedReader(inputStreamReader);
      String line=null;
      try{
        line=reader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
      while(line!=null){
        output.append(line);
        try {
          line=reader.readLine();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return output.toString();
  }

  private static URL createUrl(String requestUrl) {
    URL url=null;
    try {
      url=new URL(requestUrl);
    } catch (MalformedURLException e) {
      Log.e(LOG_TAG,String.valueOf(R.string.ERROR_URL));
    }
    return url;
  }
}
