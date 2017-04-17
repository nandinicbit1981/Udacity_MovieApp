package parimi.com.movieapp;

/**
 * Created by nandpa on 4/15/17.
 */

public class Trailer {
    String id;
    String iso_639_1;
    String iso_3166_1;
    String key;
    String name;
    String site;
    String size;
    String type;


    public Trailer() {

    }
    public Trailer(String id,
                   String iso_639_1,
                   String iso_3166_1,
                   String key,
                   String name,
                   String site,
                   String size,
                   String type)
        {
            this.id = id;
            this.key = key;
            this.site = site;
            this.type = type;
            this.name = name;
            this.iso_639_1 = iso_639_1;
            this.iso_3166_1 = iso_3166_1;
            this.size = size;
        }

}
