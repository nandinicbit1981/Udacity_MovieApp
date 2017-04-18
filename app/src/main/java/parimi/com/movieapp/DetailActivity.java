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
import parimi.com.movieapp.adapter.ReviewAdapter;
import parimi.com.movieapp.adapter.TrailerAdapter;
import parimi.com.movieapp.data.MovieContract;
import parimi.com.movieapp.model.MovieDB;
import parimi.com.movieapp.model.Review;
import parimi.com.movieapp.model.Trailer;
import parimi.com.movieapp.utils.HttpUtils;
import parimi.com.movieapp.utils.MovieUtils;
import parimi.com.movieapp.utils.PreferenceUtils;

import static parimi.com.movieapp.utils.Constants.BASE_IMAGE_URL;

public class DetailActivity extends AppCompatActivity {

    JSONObject movie;

    @Bind(R.id.favorite_btn)
    CheckBox favoriteBtn;

    String[] projection = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE,
            MovieContract.MovieEntry.TITLE,
            MovieContract.MovieEntry.POSTER_PATH};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String movieInfo = getIntent().getExtras().get(getString(R.string.movie_info)).toString();

        try {
            movie = new JSONObject(movieInfo);
            ImageView iconView = (ImageView) findViewById(R.id.movie_image);

            String url = movie.get(getString(R.string.poster_path)).toString();
            ButterKnife.bind(this);
            Glide
                    .with(getApplicationContext())
                    .load(BASE_IMAGE_URL + url)
                    .centerCrop()
                    .crossFade()
                    .into(iconView);

            if (PreferenceUtils.getFavoritesPreference(getBaseContext())) {
                RequestParams rp = new RequestParams();
                HttpUtils.getMovieById(movie.get(getString(R.string.movie_id)).toString(), rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {

                            movie = response;
                            // as the response has id of the movie in the "id" field, explicitly setting the id.
                            movie.put(getString(R.string.movie_id), response.getString("id"));
                            updateDetails();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                updateDetails();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.favorite_btn)
    public void clickFavBtn() {
        try {
            ContentValues values = new ContentValues();
            MovieDB movieDB = dbFavRecord();

            values.put(MovieContract.MovieEntry.COLUMN_MOVIE, movie.getString(getString(R.string.movie_id)));
            values.put(MovieContract.MovieEntry.TITLE, movie.getString(getString(R.string.title)));
            values.put(MovieContract.MovieEntry.POSTER_PATH, movie.getString(getString(R.string.poster_path)));
            if (movieDB != null && String.valueOf(movieDB.getMovie_id()).equals(movie.getString(getString(R.string.movie_id)))) {
                getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE + " = " + movieDB.getMovie_id(),
                        null
                );
            } else {
                getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /***
     * Check if the movie in context is favorite.
     * @return
     */

    private MovieDB dbFavRecord() {
        MovieDB movieDB = null;
        try {
            Cursor cursor = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE, MovieContract.MovieEntry.TITLE, MovieContract.MovieEntry.POSTER_PATH}, // nandini
                    MovieContract.MovieEntry.COLUMN_MOVIE + " = " + String.valueOf(movie.get(getString(R.string.movie_id))),
                    null,
                    null);

            if (cursor.moveToFirst()) {
                movieDB = MovieUtils.cursorToMovie(cursor);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return movieDB;
    }


    /**
     * this method gets the list of reviews from movieDB api
     * @param id
     */
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

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /**
     * creates list of the reviews for the movie in context. This is used to update the details activity
     * @param jsonArray
     * @return
     */

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


    /**
     * this method gets the list of trailers from movieDB api
     * @param id
     */
    private void getTrailerList(String id) {
        HttpUtils.getTrailers(id, new RequestParams(), new JsonHttpResponseHandler() {
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

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }


    /**
     * creates list of the trailers for the movie in context. This is used to update the details activity
     * @param jsonArray
     * @return
     */
    public ArrayList<Trailer> createTrailersList(JSONArray jsonArray) {
        ArrayList<Trailer> trailerArrayList = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Trailer trailer = new Trailer(
                        jsonObject.getString("id"),
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

    /**
     * This method updates the ui with data.
     * I have separated this part of the code as this will be required both when api is called or when local database is queried.
     */
    public void updateDetails() {
        try {
            String dateOfRelease = movie.get(getString(R.string.release_date)).toString();
            dateOfRelease = dateOfRelease.split("-")[0];
            ((TextView) findViewById(R.id.title)).setText(movie.get(getString(R.string.title)).toString());
            ((TextView) findViewById(R.id.release_date)).setText(dateOfRelease);
            ((TextView) findViewById(R.id.vote_average)).setText(movie.get(getString(R.string.vote_average)).toString() + "/10");
            ((TextView) findViewById(R.id.plotSynopsis)).setText(movie.get(getString(R.string.overview)).toString());
            getTrailerList(String.valueOf(movie.get(getString(R.string.movie_id))));
            getReviewList(String.valueOf(movie.get(getString(R.string.movie_id))));
            isFavoriteMovie();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method sets the favorite star button yellow , if the movie is in the current database.
     */

    public void isFavoriteMovie() {
        try {
            // if favorite setting is NOT enabled
            if (!PreferenceUtils.getFavoritesPreference(getBaseContext())) {

                //check to see if the movie exists in the local database
                Cursor cursor = getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE + " = " + String.valueOf(movie.get(getString(R.string.movie_id))),
                        null,
                        null);

                if (cursor.moveToFirst()) {
                    favoriteBtn.setChecked(true);
                } else {
                    favoriteBtn.setChecked(false);
                }
                cursor.close();
            } else { // if favorite setting is enabled
                favoriteBtn.setChecked(true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
