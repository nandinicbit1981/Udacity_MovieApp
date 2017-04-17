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
    private Boolean showFavorites = false;
    private RecyclerView recyclerView;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private String LOG_TAG = MainActivityFragment.class.getCanonicalName();

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


    public void getMovieList() {
        RequestParams rp = new RequestParams();
        String movieSorting = PreferenceUtils.getSortPreference(getActivity());
        Boolean showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
        if(!showFavorites) {
            HttpUtils.getImage(movieSorting, rp, new JsonHttpResponseHandler() {
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
            String[] projection = {
                    MovieContract.MovieEntry._ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE,
                    MovieContract.MovieEntry.TITLE,
                    MovieContract.MovieEntry.POSTER_PATH};
                    Cursor movieCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, projection, null, null, null);
                    if(movieCursor.moveToFirst()) {
                        while(!movieCursor.isAfterLast()) {
                            MovieDB movieDB = MovieUtils.cursorToMovie(movieCursor);
                            Movie movie = MovieUtils.movieDBtoMovie(movieDB);
                            results.add(movie);
                            movieCursor.moveToNext();
                        }

                    }
            updateMovieAdapter();
        }
    }

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
    @Override
    public void onResume() {
        if (!sortOrder.equals(PreferenceUtils.getSortPreference(getActivity())) || showFavorites != PreferenceUtils.getFavoritesPreference(getActivity())) {
            sortOrder = PreferenceUtils.getSortPreference(getActivity());
            showFavorites = PreferenceUtils.getFavoritesPreference(getActivity());
            getMovieList();
        }
        super.onResume();
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        if (savedInstanceState != null) {
            recyclerView = ((RecyclerView) rootView.findViewById(R.id.my_recycler_view));
            mCurrentPosition = savedInstanceState.getInt(STATE_POSITION);
            results = savedInstanceState.<Movie>getParcelableArrayList(MOVIE_LIST);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            sortOrder = PreferenceUtils.getSortPreference(getActivity());
            movieAdapter = createAdapter(results);
            recyclerView.setAdapter(movieAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), UiUtils.calculateNoOfColumns(getActivity())));
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private MovieAdapter createAdapter(ArrayList<Movie> results) {
        return new MovieAdapter(results, getActivity().getApplicationContext(), new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie item) {
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

