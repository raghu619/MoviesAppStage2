package com.example.daou5____.moviesappstage1;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.daou5____.moviesappstage1.adapter.ReviewsAdapter;
import com.example.daou5____.moviesappstage1.adapter.TrailerAdapter;
import com.example.daou5____.moviesappstage1.api.Client;
import com.example.daou5____.moviesappstage1.api.Service;
import com.example.daou5____.moviesappstage1.data.FavoritesDbHelper;
import com.example.daou5____.moviesappstage1.model.Movie;
import com.example.daou5____.moviesappstage1.model.Reviews;
import com.example.daou5____.moviesappstage1.model.ReviewsResponse;
import com.example.daou5____.moviesappstage1.model.Trailer;
import com.example.daou5____.moviesappstage1.model.TrailerResponse;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.github.ivbaranov.mfb.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataActivity extends AppCompatActivity {

    TextView movieTitle, movieOverview, movieRating, movieReleased;
    ImageView imageView;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerList;
    private ReviewsAdapter reviewsAdapter;
    private List<Reviews> reviewsList;
    private Movie favorites;
    private FavoritesDbHelper favoritesDbHelper;
    private final AppCompatActivity activity = DataActivity.this;

    Movie movie;
    String thumbnail, title, overview, rating, released;
    int movie_id;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        //find views

        imageView = (ImageView) findViewById(R.id.movie_icon);
        movieTitle = (TextView) findViewById(R.id.title_tv);
        movieOverview = (TextView) findViewById(R.id.overview_tv);
        movieRating = (TextView) findViewById(R.id.rating_tv);
        movieReleased = (TextView) findViewById(R.id.released_tv);

        Intent intentStarter = getIntent();
        if (intentStarter.hasExtra("movies")) {
            movie = getIntent().getParcelableExtra("movies");

            thumbnail = movie.getPosterPath();
            title = movie.getOriginalTitle();
            overview = movie.getOverview();
            rating = Double.toString(movie.getVoteAverage());
            released = movie.getReleaseDate();
            movie_id = movie.getId();

            String poster = "https://image.tmdb.org/t/p/w500" + thumbnail;

            Glide.with(this).load(poster).into(imageView);

            movieTitle.setText(title);
            movieOverview.setText(overview);
            movieRating.setText(rating);
            movieReleased.setText(released);
        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }

        final MaterialFavoriteButton materialFavoriteButtonNice =
                (MaterialFavoriteButton) findViewById(R.id.favorites);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        materialFavoriteButtonNice.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener(){
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                        if (favorite){
                            SharedPreferences.Editor editor = getSharedPreferences("com.example.daou5____.moviesappstage1", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Added", true);
                            editor.commit();
                            saveFavorites();
                            Snackbar.make(buttonView, "Added to Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }else{
                            int movie_id = getIntent().getExtras().getInt("id");
                            favoritesDbHelper = new FavoritesDbHelper(DataActivity.this);
                            favoritesDbHelper.deleteFavorite(movie_id);

                            SharedPreferences.Editor editor = getSharedPreferences("com.example.daou5____.moviesappstage1", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Removed", true);
                            editor.commit();
                            Snackbar.make(buttonView, "Removed from Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initViews();
        initViews2();
    }


    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.details));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initViews() {
        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(trailerLayoutManager);
        recyclerView.setAdapter(trailerAdapter);
        trailerAdapter.notifyDataSetChanged();

        loadJSON();
    }

    private void initViews2() {
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, reviewsList);

        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(reviewLayoutManager);
        recyclerView2.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();

        loadJSON2();
    }

    private void loadJSON() {

        try {
            if (BuildConfig.MOVIE_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.MOVIE_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DataActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJSON2() {

        try {
            if (BuildConfig.MOVIE_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<ReviewsResponse> call = apiService.getMovieReviews(movie_id, BuildConfig.MOVIE_API_TOKEN);
            call.enqueue(new Callback<ReviewsResponse>() {
                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    List<Reviews> reviews = response.body().getResults();
                    recyclerView2.setAdapter(new ReviewsAdapter(getApplicationContext(), reviews));
                    recyclerView2.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DataActivity.this, "Error fetching reviews data", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveFavorites(){
        favoritesDbHelper = new FavoritesDbHelper(activity);
        favorites = new Movie();

        Double rate = movie.getVoteAverage();


        favorites.setId(movie_id);
        favorites.setOriginalTitle(title);
        favorites.setPosterPath(thumbnail);
        favorites.setVoteAverage(rate);
        favorites.setOverview(overview);

        favoritesDbHelper.addFavorite(favorites);
    }
}