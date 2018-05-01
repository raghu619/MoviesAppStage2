package com.example.daou5____.moviesappstage1.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daou5____.moviesappstage1.R;
import com.example.daou5____.moviesappstage1.model.Reviews;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private Context mContext;
    private List<Reviews> reviewsList;

    public ReviewsAdapter(Context mContext, List<Reviews> reviewsList) {
        this.mContext = mContext;
        this.reviewsList = reviewsList;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_card, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        holder.content.setText(reviewsList.get(position).getContent());
        holder.reviewAuthor.setText(reviewsList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return (reviewsList != null? reviewsList.size():0);}

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView reviewAuthor;

        public ReviewsViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.review_content);
            reviewAuthor = (TextView) view.findViewById(R.id.review_author);
        }

    }
}
