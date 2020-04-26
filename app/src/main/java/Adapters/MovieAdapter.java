package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hillary.moviestage2.MainActivity;
import com.hillary.moviestage2.R;
import com.hillary.moviestage2.details_activity;
import com.squareup.picasso.Picasso;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import Data.MovieList;
import Util.Network;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<MovieList> movieLists;
    private Context context;
    private MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(List<MovieList> movieLists, Context context) {
        this.movieLists = movieLists;
        this.context = context;
    }

    public MovieAdapter(MainActivity mainActivity) {
        Context mContext = mainActivity.getApplicationContext();
    }
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MovieViewHolder(View view) {
            super(view);
            ImageView posterImageView = (ImageView) view.findViewById(R.id.imageView);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            DoubleBuffer mMoviesArrayList = null;
            assert mMoviesArrayList != null;
            double movies = mMoviesArrayList.get(adapterPosition);
            mClickHandler.onClickItem(movies);
        }
    }
    public interface MovieAdapterOnClickHandler {
        void onClickItem(double movie);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MovieList mList = movieLists.get(position);
        Picasso.get().load("https://image.tmdb.org/t/p/w185/" + (mList.getPosterUrl())).placeholder(R.drawable.empty).into(holder.posterUrl);
        holder.frameLayout.setOnClickListener(v -> {
            MovieList movieList = movieLists.get(position);
            Intent skipIntent = new Intent(v.getContext(), details_activity.class);
            skipIntent.putExtra(Network.PARCEL_KEY, new MovieList(movieList.getId(), movieList.getTitle(), movieList.getDescription(), movieList.getPosterUrl(), movieList.getVoteAverage(), movieList.getReleaseDate()));
            v.getContext().startActivity(skipIntent);
        });
    }
    public static String requestUrlForPoster(String poster) {
        Uri.Builder uriBuilder = Uri.parse("https://image.tmdb.org/t/p/w185").buildUpon().appendPath(poster);
        return uriBuilder.toString();
    }


    class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView posterUrl;
        FrameLayout frameLayout;
        private ViewHolder(View itemView) {
            super(itemView);
            posterUrl = itemView.findViewById(R.id.imageView);
            frameLayout = itemView.findViewById(R.id.favy);
        }
        public void setMovieArrayList(ArrayList<MovieList> moviesArrayList) {
            ArrayList<MovieList> mMovieArrayList = moviesArrayList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return movieLists.size();
    }
    public void setMoviesLive(List<MovieList> moviesLive) {
        this.movieLists = moviesLive;
    }
    public List<MovieList> getMoviesLive() {
        return movieLists;
    }
}
