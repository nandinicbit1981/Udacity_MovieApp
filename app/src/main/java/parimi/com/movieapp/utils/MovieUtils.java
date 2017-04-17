package parimi.com.movieapp.utils;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import parimi.com.movieapp.model.Movie;
import parimi.com.movieapp.model.MovieDB;

/**
 * Created by nandpa on 4/17/17.
 */

public class MovieUtils {

    public static MovieDB cursorToMovie(Cursor cursor) {
        MovieDB movieDb = new MovieDB();
        //movieDb.id = cursor.getLong(0);
        if(cursor.getLong(0) > -1) {
            movieDb.set_id(cursor.getLong(0));
            movieDb.setMovie_id(cursor.getInt(1));
            movieDb.setTitle(cursor.getString(2));
            movieDb.setPosterPath(cursor.getString(3));
        }
        return movieDb;
    }

    public static Movie movieDBtoMovie(MovieDB movieDB) {
        Movie movie = new Movie();
        movie.setMovie_id(movieDB.getMovie_id());
        movie.setTitle(movieDB.getTitle());
        movie.setposter_path(movieDB.getPosterPath());
        return movie;
    }


    public static ArrayList<Movie> createMovieList(JSONArray jsonArray) {
        ArrayList<Movie> movieList = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray array = jsonObject.getJSONArray("genre_ids");
                int[] genres = new int[array.length()];
                for (int j = 0; j < array.length(); j++) {
                    genres[j] = Integer.parseInt(array.get(j).toString());
                }
                Movie yourPojo = new Movie(jsonObject.getString("poster_path"),
                        jsonObject.getBoolean("adult"),
                        jsonObject.getString("overview"),
                        jsonObject.getString("release_date"),
                        genres,
                        jsonObject.getInt("id"),
                        jsonObject.getString("original_title"),
                        jsonObject.getString("original_language"),
                        jsonObject.getString("title"),
                        jsonObject.getString("backdrop_path"),
                        jsonObject.getLong("popularity"),
                        jsonObject.getInt("vote_count"),
                        jsonObject.getBoolean("video"),
                        jsonObject.getLong("vote_average")
                );
                movieList.add(yourPojo);
            }

        } catch (Exception e) {

        }
        return movieList;
    }

}
