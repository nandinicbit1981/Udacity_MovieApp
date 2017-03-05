package parimi.com.movieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import parimi.com.movieapp.R;

public class DetailActivity extends AppCompatActivity {

    JSONObject movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String movieInfo = getIntent().getExtras().get("movieInfo").toString();

        try {
           movie = new JSONObject(movieInfo);
            ImageView iconView = (ImageView) findViewById(R.id.movie_image);

            String url = movie.get("poster_path").toString();

            Glide
                    .with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w185/" + url)
                    .centerCrop()
                    .crossFade()
                    .into(iconView);

            String dateOfRelease = movie.get("release_date").toString();
            dateOfRelease = dateOfRelease.split("-")[0];
            ((TextView)findViewById(R.id.title)).setText(movie.get("title").toString());
            ((TextView)findViewById(R.id.release_date)).setText(dateOfRelease);
            ((TextView)findViewById(R.id.vote_average)).setText(movie.get("vote_average").toString() + "/10");
            ((TextView)findViewById(R.id.plotSynopsis)).setText(movie.get("overview").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
