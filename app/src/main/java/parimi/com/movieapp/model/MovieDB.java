package parimi.com.movieapp.model;

import static android.R.attr.id;

/**
 * Created by nandpa on 4/16/17.
 */

public class MovieDB {

    private Long _id;
    private int movie_id; // this is the movie id, i am naming this as id in order to match the Movie Object
    private String title;
    private String posterPath;

    public MovieDB() {}

     public MovieDB(Long _id, int movie_id,  String title, String posterPath) {
         this._id = _id;
         this.movie_id = id;
         this.title = title;
         this.posterPath = posterPath;
     }


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
