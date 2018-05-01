package com.example.daou5____.moviesappstage1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.daou5____.moviesappstage1.adapter.MoviesAdapter;

import com.example.daou5____.moviesappstage1.data.MoviesDbHelper;
import com.example.daou5____.moviesappstage1.model.Movie;
import com.example.daou5____.moviesappstage1.api.Client;
import com.example.daou5____.moviesappstage1.api.Service;
import com.example.daou5____.moviesappstage1.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int POPULAR = 1;
    public static final int TOP_MOVIES = 2;
    public static final int FAVORITES = 3;
    public static final int COMING_SOON = 4;
    public static final int NOW_PLAYING = 5;
    int selection = -1;

    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    private List<Movie> moviesList;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;
    private Movie favorites;
    private MoviesDbHelper moviesDbHelper;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        if (!isOnline()) {
            Toast.makeText(activity, "No Connection", Toast.LENGTH_SHORT).show();
        }

        initViews();
        recyclerView = ( RecyclerView ) findViewById( R.id.recycler_view );
        moviesDbHelper = new MoviesDbHelper(activity);
        if (savedInstanceState != null) {
            selection = savedInstanceState.getInt("selection");
            //Now just load the list
            switch (selection) {
                case POPULAR:
                    loadJSON();
                    break;
                case TOP_MOVIES:
                    loadJSON();
                    break;
                case FAVORITES:
                    initViews2();
                    break;
                case COMING_SOON:
                    loadJSON3();
                    break;
                case NOW_PLAYING:
                    loadJSON4();
                    break;
                case -1:
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //This method will be called when the device is rotated.
        //In this method, we will save the values we want to save.
        outState.putInt("selection", selection);//This will save the selected selection for the list.
        //Now, to access this selection we can do that in OnCreate() method.
    }


    public Activity getActivity() {
        Context context = this;
        while ( context instanceof ContextWrapper ) {
            if ( context instanceof Activity ) {
                return ( Activity ) context;
            }
            context = ( ( ContextWrapper ) context ).getBaseContext();
        }
        return null;
    }

    private void initViews() {

        recyclerView = ( RecyclerView ) findViewById( R.id.recycler_view );

        moviesList = new ArrayList<>();
        mAdapter = new MoviesAdapter( this, moviesList );

        if ( getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //portraitView
            recyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        }else {
            //landscapeView
            recyclerView.setLayoutManager( new GridLayoutManager( this, 4 ) );
        }

        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter( mAdapter );
        mAdapter.notifyDataSetChanged();
        moviesDbHelper = new MoviesDbHelper( activity );

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        checkSortOrder();
    }

    private void initViews2(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        moviesList = new ArrayList<>();
        mAdapter = new MoviesAdapter(this, moviesList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        moviesDbHelper = new MoviesDbHelper(activity);

        getAllFavorite();
    }

    private void loadJSON() {

        try {
            if ( BuildConfig.MOVIE_API_TOKEN.isEmpty() ) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create( Service.class );
            Call< MoviesResponse > call = apiService.getPopularMovies( BuildConfig.MOVIE_API_TOKEN );
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJSON2() {

        try {
            if ( BuildConfig.MOVIE_API_TOKEN.isEmpty() ) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create( Service.class );
            Call< MoviesResponse > call = apiService.getTopRatedMovies( BuildConfig.MOVIE_API_TOKEN );
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJSON3() {

        try {
            if ( BuildConfig.MOVIE_API_TOKEN.isEmpty() ) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create( Service.class );
            Call< MoviesResponse > call = apiService.getUpcomingMovies( BuildConfig.MOVIE_API_TOKEN );
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJSON4() {

        try {
            if ( BuildConfig.MOVIE_API_TOKEN.isEmpty() ) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create( Service.class );
            Call< MoviesResponse > call = apiService.getNowPlayingMovies( BuildConfig.MOVIE_API_TOKEN );
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_settings:
                Intent intent = new Intent( this, SettingsActivity.class);
                startActivity( intent );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
        Log.d( LOG_TAG, "Preferences updated" );
        checkSortOrder();
    }

    private void checkSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        String sortOrder = preferences.getString( this.getString( R.string.order_by_key),
        this.getString( R.string.sort_most_popular ));

       if (sortOrder.equals(this.getString(R.string.sort_most_popular))) {
        Log.d(LOG_TAG, "Sorting by most popular");
        loadJSON();
       } else if (sortOrder.equals(this.getString(R.string.sort_favorites))){
           Log.d(LOG_TAG, "Sorting by favorite");
           initViews2();
       } else if (sortOrder.equals(this.getString(R.string.sort_upcoming))){
           Log.d(LOG_TAG, "Sorting by favorite");
           loadJSON3();
       } else if (sortOrder.equals(this.getString(R.string.sort_now_playing))){
           Log.d(LOG_TAG, "Sorting by favorite");
           loadJSON4();
       } else {
        Log.d(LOG_TAG, "Sorting by release date");
        loadJSON2();
       }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (moviesList.isEmpty()) {
            checkSortOrder();
        } else {
            checkSortOrder();
        }
    }

    private void getAllFavorite(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                moviesList.clear();
                moviesList.addAll(moviesDbHelper.getAllFavorite());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
