package parimi.com.movieapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity {

    JSONObject movie;

    @Bind(R.id.favorite_btn)
    CheckBox favoriteBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String movieInfo = getIntent().getExtras().get("movieInfo").toString();

        try {
            movie = new JSONObject(movieInfo);
            ImageView iconView = (ImageView) findViewById(R.id.movie_image);

            String url = movie.get("poster_path").toString();
            ButterKnife.bind(this);
            Glide
                    .with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w185/" + url)
                    .centerCrop()
                    .crossFade()
                    .into(iconView);

            String dateOfRelease = movie.get("release_date").toString();
            dateOfRelease = dateOfRelease.split("-")[0];
            ((TextView) findViewById(R.id.title)).setText(movie.get("title").toString());
            ((TextView) findViewById(R.id.release_date)).setText(dateOfRelease);
            ((TextView) findViewById(R.id.vote_average)).setText(movie.get("vote_average").toString() + "/10");
            ((TextView) findViewById(R.id.plotSynopsis)).setText(movie.get("overview").toString());
            getTrailerList(String.valueOf(movie.get("id")));
            getReviewList(String.valueOf(movie.get("id")));
            String[] projection = {
                    MovieContract.MovieEntry._ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE,
                    MovieContract.MovieEntry.FAVORITE};

            Cursor cursor = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE, MovieContract.MovieEntry.FAVORITE},
                    MovieContract.MovieEntry.COLUMN_MOVIE + " = " + String.valueOf(movie.get("id")),
                    null,
                    null);

            Cursor cursor1 = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                   null,
                    null,
                    null,
                    null);

            System.out.println(cursor1);
            if(cursor.moveToFirst()) {
               MovieDB movieDB = cursorToMovie(cursor);
                if(movieDB.favorite != null) {
                    favoriteBtn.setChecked(movieDB.favorite);
                } else {
                    favoriteBtn.setChecked(false);
                }
            } else {
                favoriteBtn.setChecked(false);
            }
            cursor.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private MovieDB cursorToMovie(Cursor cursor) {
        MovieDB movieDb = new MovieDB();
        //movieDb.id = cursor.getLong(0);
        if(cursor.getLong(0) > -1) {
            movieDb.movie = cursor.getString(1);
            movieDb.favorite = (cursor.getInt(2) == 1);
        }
        return movieDb;
    }

    @OnClick(R.id.favorite_btn)
    public void clickFavBtn() {
        try {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE, movie.getString("id"));
            values.put(MovieContract.MovieEntry.FAVORITE, favoriteBtn.isChecked());
            if(dbFavRecord() != null && dbFavRecord().movie == movie.getString("id")) {
                getContentResolver().update( MovieContract.MovieEntry.CONTENT_URI,
                        values,
                        MovieContract.MovieEntry.COLUMN_MOVIE + " = " + String.valueOf(movie.get("id")),
                        null
                );
            } else {
                getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            }
        } catch (Exception e) {

        }
    }


    private MovieDB dbFavRecord() {
      MovieDB movieDB = null;
        try {
            Cursor cursor = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE, MovieContract.MovieEntry.FAVORITE},
                    MovieContract.MovieEntry.COLUMN_MOVIE + " = " + String.valueOf(movie.get("id")),
                    null,
                    null);


                movieDB = cursorToMovie(cursor);

        }catch (Exception e) {
            System.out.println(e);
        }
        return movieDB;
    }

    private void getReviewList(String id) {
        HttpUtils.getReviews(String.valueOf(id), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Review> reviewArrayList = createReviewsList(response.getJSONArray("results"));
                    ReviewAdapter adapter = new ReviewAdapter(getBaseContext(), reviewArrayList);
                    ListView listView = (ListView) findViewById(R.id.review_list);
                    listView.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrayList<Review> createReviewsList(JSONArray jsonArray) {
        ArrayList<Review> reviewArrayList = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Review review = new Review(
                        jsonObject.getString("id"),
                        jsonObject.getString("author"),
                        jsonObject.getString("content"),
                        jsonObject.getString("url")
                );
                reviewArrayList.add(review);
            }

        } catch (Exception e) {

        }
        return reviewArrayList;
    }


    private void getTrailerList(String id){
        HttpUtils.getTrailers(String.valueOf(id), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Trailer> trailerArrayList = createTrailersList(response.getJSONArray("results"));
                    TrailerAdapter adapter = new TrailerAdapter(getBaseContext(), trailerArrayList);
                    ListView listView = (ListView) findViewById(R.id.trailer_list);
                    listView.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrayList<Trailer> createTrailersList(JSONArray jsonArray) {
        ArrayList<Trailer> trailerArrayList = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Trailer trailer = new Trailer(jsonObject.getString("id"),
                        jsonObject.getString("iso_639_1"),
                        jsonObject.getString("iso_3166_1"),
                        jsonObject.getString("key"),
                        jsonObject.getString("name"),
                        jsonObject.getString("site"),
                        jsonObject.getString("size"),
                        jsonObject.getString("type")
                );
                trailerArrayList.add(trailer);
            }

        } catch (Exception e) {

        }
        return trailerArrayList;
    }


}
