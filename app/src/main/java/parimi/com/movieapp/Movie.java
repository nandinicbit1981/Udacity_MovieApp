package parimi.com.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nandpa on 3/2/17.
 */

public class Movie implements Parcelable{
    String poster_path;
    boolean adult;
    String overview;
    String release_date;
    int[] genre_ids;
    int id;
    String original_title;
    String original_language;
    String title;
    String backdrop_path;
    float popularity;
    int vote_count;
    boolean video;
    float vote_average;

    public Movie(String poster_path,
            boolean adult,
            String overview,
            String release_date,
            int[] genre_ids,
            int id,
            String original_title,
            String original_language,
            String title,
            String backdrop_path,
            float popularity,
            int vote_count,
            boolean video,
            float vote_average)
    {
        this.poster_path = poster_path;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
        this.genre_ids = genre_ids;
        this.id = id;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.video = video;
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(poster_path);
        parcel.writeInt(adult ? 1 : 0);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeIntArray(genre_ids);
        parcel.writeInt(id);
        parcel.writeString(original_title);
        parcel.writeString(original_language);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeFloat(popularity);
        parcel.writeInt(vote_count);
        parcel.writeInt(video ? 1 : 0);
        parcel.writeFloat(vote_average);
    }
}
