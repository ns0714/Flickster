package com.flickster.activities;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flickster.Config;
import com.flickster.R;
import com.flickster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    String videoKey;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie movie = (Movie)getIntent().getSerializableExtra("movie");

        TextView title = (TextView) findViewById(R.id.tvMovieTitle);
        TextView releaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        TextView synopsis = (TextView) findViewById(R.id.tvSynopsis);

        title.setText(movie.getOriginalTitle());
        releaseDate.setText("Release Date:  " + movie.getReleaseDate());
        ratingBar.setRating((float) movie.getVoteAverage()/2f);
        synopsis.setText(movie.getOverview());

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.moviePlayer);

        String url = String.format(
                "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed",
                movie.getMovieId());

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray movieJsonResults = null;
                try {
                    movieJsonResults = response.getJSONArray("results");
                    if(movieJsonResults.length()>0) {
                        videoKey = movieJsonResults.getJSONObject(0).getString("key");
                        youTubePlayerView.initialize(Config.YT_API_KEY,
                                new YouTubePlayer.OnInitializedListener(){
                                    @Override
                                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                        YouTubePlayer youTubePlayer, boolean b) {
                                        youTubePlayer.cueVideo(videoKey);
                                    }

                                    @Override
                                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                        YouTubeInitializationResult youTubeInitializationResult) {
                                        Toast.makeText(MovieDetailsActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }
}