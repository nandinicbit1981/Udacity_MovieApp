package parimi.com.movieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> mDataset;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView iconView;

        public ViewHolder(View v) {
            super(v);
            iconView = (ImageView) v.findViewById(R.id.flavor_image);
        }

        public void bind(final Movie item, final OnItemClickListener listener) {
            String url = item.poster_path;
            Glide
                    .with(context)
                    .load("http://image.tmdb.org/t/p/w185/" + url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .centerCrop()
                    .crossFade()
                    .into(iconView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    public void add(int position, Movie item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Movie item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }


    public MovieAdapter(ArrayList<Movie> movies, Context context, OnItemClickListener listener) {
        mDataset = movies;
        this.context = context;
        this.listener = listener;
    }

    public void swapData(ArrayList<Movie> results) {
        mDataset = results;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flavor_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mDataset.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
