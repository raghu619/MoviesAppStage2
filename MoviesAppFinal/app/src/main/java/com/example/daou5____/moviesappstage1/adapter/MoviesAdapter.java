package com.example.daou5____.moviesappstage1.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daou5____.moviesappstage1.DataActivity;
import com.example.daou5____.moviesappstage1.R;
import com.example.daou5____.moviesappstage1.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter< MoviesAdapter.MyViewHolder > {

    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter ( Context mContext, List< Movie > movieList ) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MoviesAdapter.MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType ) {

        View view = LayoutInflater.from( viewGroup.getContext()).inflate( R.layout.movie_card, viewGroup, false );
        return new MyViewHolder ( view );
    }

    @Override
    public void onBindViewHolder( final MoviesAdapter.MyViewHolder holder, int position ) {

        holder.title.setText( movieList.get( position ).getOriginalTitle() );
        String vote = Double.toString( movieList.get( position ).getVoteAverage() );
        holder.rating.setText( vote );

        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get( position ).getPosterPath();

        Picasso.with( mContext ).load( poster ).into( holder.thumbnail );
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, rating;
        public ImageView thumbnail;


        public MyViewHolder ( View view ) {
            super( view );

            title = ( TextView ) view.findViewById( R.id.title_tv );
            rating = ( TextView ) view.findViewById( R.id.rating_tv );
            thumbnail = ( ImageView ) view.findViewById( R.id.thumbnail );
            //on item click
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(mContext, DataActivity.class);
                        intent.putExtra("movies", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}