package parimi.com.movieapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import parimi.com.movieapp.R;
import parimi.com.movieapp.model.Review;

/**
 * Created by nandpa on 4/16/17.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final int itemPos = position;
        final Review review = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_review_item, parent, false);
        }
        // Lookup view for data population
        TextView authorName = (TextView) convertView.findViewById(R.id.author);

        // Populate the data into the template view using the data object
        authorName.setText(review.getAuthor());

        TextView content = (TextView) convertView.findViewById(R.id.review_content);
        content.setText(review.getContent());

        // Return the completed view to render on screen
        return convertView;
    }

}
