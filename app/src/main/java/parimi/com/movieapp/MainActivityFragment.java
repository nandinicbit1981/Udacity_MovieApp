package parimi.com.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment containing the list view of Android versions.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private View rootView;
    private static String STATE_POSITION = "STATE_POSITION";
    private static String MOVIE_LIST = "MOVIE_LIST";
    int mCurrentPosition = 0;
    private ArrayList<Movie> results = null;
    private String sortOrder = "";
    private RecyclerView recyclerView;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

            savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
            savedInstanceState.putInt(STATE_POSITION, mCurrentPosition);
            savedInstanceState.putParcelableArrayList(MOVIE_LIST, results);


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
        String movieSorting = getSortPreference();
        HttpUtils.getImage(movieSorting, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    results = createMovieList(response.getJSONArray("results"));
                    if(movieAdapter ==null){
                        movieAdapter = createAdapter(results);
                    }
                    recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity())));
                    recyclerView.setAdapter(movieAdapter);
                    movieAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        if(sortOrder != "" && sortOrder != getSortPreference()) {
            getMovieList();
        }
        super.onResume();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        if(savedInstanceState != null) {
            recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
            mCurrentPosition = savedInstanceState.getInt(STATE_POSITION);
            results = savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            movieAdapter = createAdapter(results);
            recyclerView.setAdapter(movieAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity())));
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
        if(savedInstanceState != null && savedInstanceState.getParcelableArrayList(MOVIE_LIST) != null) {
            movieAdapter = createAdapter(savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST));
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity())));
            recyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        } else {
            getMovieList();
        }
        return rootView;
    }


    private String getSortPreference() {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MoviePrefs", MODE_PRIVATE);
        return sharedPreferences.getString("sortOptions","popular");
    }

    private MovieAdapter createAdapter(ArrayList<Movie> results) {
        return new MovieAdapter(results, getActivity().getApplicationContext(),new MovieAdapter.OnItemClickListener() {
            @Override public void onItemClick(Movie item) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                try {
                    Gson gson = new Gson();
                    String movieJson = gson.toJson(item);
                    intent.putExtra("movieInfo", movieJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().startActivity(intent);
            }
        });
    }

}


class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}