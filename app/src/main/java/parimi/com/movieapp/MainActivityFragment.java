package parimi.com.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import parimi.com.movieapp.adapter.MovieAdapter;
import parimi.com.movieapp.data.MovieContract;
import parimi.com.movieapp.model.Movie;
import parimi.com.movieapp.model.MovieDB;
import parimi.com.movieapp.utils.HttpUtils;
import parimi.com.movieapp.utils.MovieUtils;
import parimi.com.movieapp.utils.PreferenceUtils;
import parimi.com.movieapp.utils.UiUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment containing the recycler list view of Movies.
 */

public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private View rootView;
    private static String STATE_POSITION = "STATE_POSITION";
    private static String MOVIE_LIST = "MOVIE_LIST";
    int mCurrentPosition = 0;
    private ArrayList<Movie> results = null;
    private String sortOrder = "";
    private Boolean showFavorites = false;
    private RecyclerView recyclerView;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private String LOG_TAG = MainActivityFragment.class.getCanonicalName();
    private String[] projection = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE,
            MovieContract.MovieEntry.TITLE,
            MovieContract.MovieEntry.POSTER_PATH};

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = PreferenceUtils.getSortPreference(getActivity());
        showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(MOVIE_LIST) != null) {
            movieAdapter = createAdapter(savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST));
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), UiUtils.calculateNoOfColumns(getActivity())));
            recyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
            mCurrentPosition = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE).getInt("currentRVPosition", 0);
            recyclerView.scrollToPosition(mCurrentPosition);
        } else {
            getMovieList();
        }
        return rootView;
    }

    /**
     * This method saves current scroll position, current movie list.
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
            savedInstanceState.putParcelableArrayList(MOVIE_LIST, results);
            mCurrentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("currentRVPosition", mCurrentPosition);
            editor.commit();
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
        }

    }


    /**
     * This method gets the movies from either api or from the local db based on the preferences set.
     * When favorites setting is enabled, movies info needs to be queried from local database.
     * When its not, the data comes from api.
     */
    public void getMovieList() {
        RequestParams rp = new RequestParams();
        String movieSorting = PreferenceUtils.getSortPreference(getActivity());
        Boolean showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
        if (!showFavorites) {
            HttpUtils.getMoviesBySortPref(movieSorting, rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        results = MovieUtils.createMovieList(response.getJSONArray("results"));
                        updateMovieAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            results = new ArrayList<>();

            Cursor movieCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, projection, null, null, null);
            if (movieCursor.moveToFirst()) {
                while (!movieCursor.isAfterLast()) {
                    MovieDB movieDB = MovieUtils.cursorToMovie(movieCursor);
                    Movie movie = MovieUtils.movieDBtoMovie(movieDB);
                    results.add(movie);
                    movieCursor.moveToNext();
                }

            }
            updateMovieAdapter();
        }
    }


    /**
     * This method updates the adapter accordingly.
     * There are two scenarios :
     * <p>
     * a. When all the data from the api is displayed
     * b. When the favorite setting is selected, so only favorite movies are shown on the screen.
     * <p>
     * In both the cases, the adapter, recycler view, current scroll position etc needs to be set appropriately, and that is handled in this method.
     */
    public void updateMovieAdapter() {
        if (movieAdapter == null) {
            movieAdapter = createAdapter(results);
        }
        movieAdapter.swapData(results);
        recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), UiUtils.calculateNoOfColumns(getActivity())));
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
        mCurrentPosition = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE).getInt("currentRVPosition", 0);
        recyclerView.scrollToPosition(mCurrentPosition);
    }


    /**
     * This method is called when the back button is clicked between the screens.
     * Updating the data whenever the preferences are changed.
     */
    @Override
    public void onResume() {
        if (!sortOrder.equals(PreferenceUtils.getSortPreference(getActivity())) || showFavorites != PreferenceUtils.getFavoritesPreference(getActivity())) {
            sortOrder = PreferenceUtils.getSortPreference(getActivity());
            showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
            getMovieList();
        }
        super.onResume();
    }


    /**
     * Restore UI state from the savedInstanceState.
     * This method updates the scroll position, sort preferences, recyclerview, adapter appropriately.
     *
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
            mCurrentPosition = savedInstanceState.getInt(STATE_POSITION);
            results = savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            sortOrder = PreferenceUtils.getSortPreference(getActivity());
            showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
            movieAdapter = createAdapter(results);
            recyclerView.setAdapter(movieAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), UiUtils.calculateNoOfColumns(getActivity())));
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }


    /**
     * Creating a movie adapter and setting onclick listener for each item in the adapter
     *
     * @param results - contain array of results either from the api or from the local database
     * @return MovieAdapter
     */
    private MovieAdapter createAdapter(ArrayList<Movie> results) {
        return new MovieAdapter(results, getActivity().getApplicationContext(), new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie item) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                try {
                    Gson gson = new Gson();
                    String movieJson = gson.toJson(item);
                    intent.putExtra(getString(R.string.movie_info), movieJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().startActivity(intent);
            }
        });
    }

}

