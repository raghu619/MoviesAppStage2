package com.example.daou5____.moviesappstage1.api;

import com.example.daou5____.moviesappstage1.model.MoviesResponse;
import com.example.daou5____.moviesappstage1.model.ReviewsResponse;
import com.example.daou5____.moviesappstage1.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET( "movie/popular" )
    Call<MoviesResponse> getPopularMovies(@Query( "api_key") String apiKey );

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos?")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET( "movie/{movie_id}/reviews?" )
    Call<ReviewsResponse> getMovieReviews(@Path("movie_id") int id, @Query("api_key") String apiKey);
}