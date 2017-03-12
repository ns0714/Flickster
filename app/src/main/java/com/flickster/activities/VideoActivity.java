package com.flickster.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.flickster.Config;
import com.flickster.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends YouTubeBaseActivity {

    @BindView(R.id.moviePlayer) YouTubePlayerView youTubePlayerView;

    OkHttpClient okHttpClient = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        long movieId = getIntent().getLongExtra("movieId",0l);

        String url = String.format(
                "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed",
                movieId);

        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(VideoActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray movieJsonResults = jsonObject.getJSONArray("results");
                    if(movieJsonResults.length()>0) {
                        final String videoKey = movieJsonResults.getJSONObject(0).getString("key");

                        VideoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                youTubePlayerView.initialize(Config.YT_API_KEY,
                                        new YouTubePlayer.OnInitializedListener(){
                                            @Override
                                            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                                YouTubePlayer youTubePlayer, boolean b) {
                                                youTubePlayer.loadVideo(videoKey);
                                            }

                                            @Override
                                            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                                YouTubeInitializationResult youTubeInitializationResult) {
                                                Toast.makeText(VideoActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }
}
