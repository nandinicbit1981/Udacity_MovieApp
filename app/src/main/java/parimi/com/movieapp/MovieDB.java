package parimi.com.movieapp;

/**
 * Created by nandpa on 4/16/17.
 */

public class MovieDB {

     Long id;
     String  movie;
     Boolean favorite;

    public MovieDB() {}

     public MovieDB(Long id, String movie, Boolean favorite) {
         this.id = id;
         this.movie = movie;
         this.favorite = favorite;
     }



}
