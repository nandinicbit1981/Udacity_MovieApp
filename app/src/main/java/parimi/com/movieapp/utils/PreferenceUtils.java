package parimi.com.movieapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import parimi.com.movieapp.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nandpa on 4/17/17.
 */

public class PreferenceUtils {

    public static String getSortPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.movie_prefs), MODE_PRIVATE);
        return sharedPreferences.getString("sortOptions", "popular");
    }

    public static Boolean getFavoritesPreference(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(context.getString(R.string.movie_prefs), MODE_PRIVATE);
        return sharedPreferences.getBoolean("showFavorite", false);
    }


}
