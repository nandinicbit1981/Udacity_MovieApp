package parimi.com.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import parimi.com.movieapp.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment containing the list view of Android versions.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private View rootView;
    private GridView gridView;
    private static String STATE_POSITION = "STATE_POSITION";
    private static String MOVIE_LIST = "MOVIE_LIST";
    int mCurrentPosition = 0;
    private ArrayList<Movie> results = null;

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(gridView != null) {
            mCurrentPosition = gridView.getFirstVisiblePosition();
            savedInstanceState.putInt(STATE_POSITION, mCurrentPosition);
            savedInstanceState.putParcelableArrayList(MOVIE_LIST, results);
        }

    }

    public ArrayList<Movie> createMovieList(JSONArray jsonArray) {
        ArrayList<Movie> movieList = new ArrayList<>();
       try {

           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               JSONArray array = jsonObject.getJSONArray("genre_ids");
               int[] genres = new int[array.length()];
               for(int j=0; j < array.length(); j++) {
                   genres[j] = Integer.parseInt(array.get(j).toString());
               }
               Movie yourPojo = new Movie(
                       jsonObject.getString("poster_path"),
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

       }catch (Exception e) {

       }
        return movieList;
    }
    public void getMovieList() {
        RequestParams rp = new RequestParams();
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MoviePrefs", MODE_PRIVATE);
        String movieSorting = sharedPreferences.getString("sortOptions","popular");
        HttpUtils.getImage(movieSorting, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    results = createMovieList(response.getJSONArray("results"));
                    if(movieAdapter ==null){
                        movieAdapter = new MovieAdapter(getActivity(), results);
                    }
                    gridView = ((GridView) rootView);
                    gridView.setAdapter(movieAdapter);
                    movieAdapter.notifyDataSetChanged();
                    gridView.smoothScrollToPosition(mCurrentPosition);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            try {
                                Gson gson = new Gson();
                                String movieJson = gson.toJson(results.get(position));
                                intent.putExtra("movieInfo", movieJson);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        if(savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(STATE_POSITION);
            results = savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if(savedInstanceState != null && savedInstanceState.getParcelableArrayList(MOVIE_LIST) != null) {
            movieAdapter = new MovieAdapter(getActivity(), savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST));
            gridView = ((GridView) rootView);
            gridView.setAdapter(movieAdapter);
            gridView.smoothScrollToPosition(savedInstanceState.getInt(STATE_POSITION));
        } else {
            getMovieList();
        }
        return rootView;
    }
}