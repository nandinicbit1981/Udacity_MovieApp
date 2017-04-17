package parimi.com.movieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import parimi.com.movieapp.R;
import parimi.com.movieapp.model.Trailer;

/**
 * Created by nandpa on 4/16/17.
 */

public class TrailerAdapter extends ArrayAdapter<Trailer>{

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final int itemPos = position;
        final Trailer trailer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_trailer_item, parent, false);
        }
        // Lookup view for data population
        TextView trailerName = (TextView) convertView.findViewById(R.id.trailer);

        trailerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Access the row position here to get the correct data item
                Trailer trailer1 = getItem(itemPos);

                Intent videoClient = new Intent(Intent.ACTION_VIEW);
                videoClient.setData(Uri.parse("http://m.youtube.com/watch?v="+ trailer1.getKey()));
                videoClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(videoClient);
            }
        });
        // Populate the data into the template view using the data object
        trailerName.setText(trailer.getName());

        // Return the completed view to render on screen
        return convertView;
    }

}
