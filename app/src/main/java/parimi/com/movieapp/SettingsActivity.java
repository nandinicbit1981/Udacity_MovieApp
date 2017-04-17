package parimi.com.movieapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.sortPopular)
    RadioButton sortPopularRB;

    @Bind(R.id.sortRating)
    RadioButton sortRatingRB;

    @Bind(R.id.sortMovies)
    RadioGroup sortRadioMovies;

    @Bind(R.id.favorite_chkbx)
    CheckBox showFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE);
        String selectedOption = sharedPref.getString("sortOptions", "popular");
        RadioButton sortOptionsRB;
        if (selectedOption.equals(getString(R.string.popular_option))) {
            sortOptionsRB = (RadioButton) findViewById(R.id.sortPopular);
            sortOptionsRB.setChecked(true);
        } else {
            sortOptionsRB = (RadioButton) findViewById(R.id.sortRating);
            sortOptionsRB.setChecked(true);
        }

        showFavorite.setChecked(sharedPref.getBoolean("showFavorite", false));

    }

    @OnClick(R.id.sortPopular)
    void onSortPopularClick() {
        updatePreferences(getString(R.string.popular_option));
    }

    @OnClick(R.id.sortRating)
    void onSortRatingClick() {
        updatePreferences(getString(R.string.top_rated_option));
    }


    public void updatePreferences(String prefs) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sortOptions", prefs);
        editor.commit();
    }

    @OnClick(R.id.favorite_chkbx)
    void favoriteCheckBoxClicked() {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.movie_prefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("showFavorite", showFavorite.isChecked());
        editor.commit();
    }
}
