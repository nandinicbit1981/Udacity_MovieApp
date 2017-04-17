package parimi.com.movieapp;

/**
 * Created by nandpa on 4/16/17.
 */

public class Review {
    String id;
    String author;
    String content;
    String url;


    public Review() {

    }

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }
}
