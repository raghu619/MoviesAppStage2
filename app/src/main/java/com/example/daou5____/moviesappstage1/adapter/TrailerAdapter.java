package com.example.daou5____.moviesappstage1.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.daou5____.moviesappstage1.R;
import com.example.daou5____.moviesappstage1.model.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerMyViewHolder> {

    private Context mContext;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context mContext, List<Trailer> trailerList) {
        this.mContext = mContext;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerAdapter.TrailerMyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card, parent, false);
        return new TrailerMyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerMyViewHolder holder, int position) {
        String videoClip = "http://img.youtube.com/vi/" + trailerList.get( position ).getKey() + "/0.jpg";

        Glide.with(mContext)
                .load( videoClip )
                .into( holder.thumbnail );
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailerMyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public TrailerMyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Trailer clickedDataItem = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("movie_id", videoId);
                        mContext.startActivity(intent);

                        Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}
