package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hillary.moviestage2.R;

import java.util.List;

import Data.ReviewList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<ReviewList> reviewsLists;
    private Context context;
    public ReviewAdapter(List<ReviewList> reviewsLists, Context context){
        this.reviewsLists = reviewsLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ReviewList reviewsList = reviewsLists.get(position);
        holder.authorTV.setText(reviewsList.getAuthor());
        holder.reviewTV.setText(reviewsList.getReviewContent());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTV, reviewTV;
        ViewHolder(View itemView) {
            super(itemView);
            authorTV = itemView.findViewById(R.id.authorNameTv);
            reviewTV = itemView.findViewById(R.id.contentTv);
        }
    }

    @Override
    public int getItemCount() {
        return reviewsLists.size();
    }
}