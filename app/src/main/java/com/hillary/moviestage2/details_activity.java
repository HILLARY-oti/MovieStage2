package com.hillary.moviestage2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.MovieAdapter;
import Adapters.ReviewAdapter;
import Adapters.TrailerAdapter;
import Data.MovieDB;
import Data.MovieDao;
import Data.MovieList;
import Data.ReviewList;
import Data.TrailerList;
import Util.GlobalAppExecutor;
import Util.MovieViewModel;
import Util.Network;


public class details_activity extends AppCompatActivity {
    private static final String LOG_TAG = "details_activity";
    private List<TrailerList> trailerLists;
    private TrailerAdapter adapter;
    private RecyclerView recyclerViewTr, recyclerViewRV;
    private long idVal;
    private List<ReviewList> reviewsLists;
    private ReviewAdapter adapter2;
    private TextView favTV;
    private ImageView favView;
    private boolean isFavorite = false;
    private MovieDao mMovieDao;
    private MovieList movieList;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_activity);
        mMovieDao = Room.databaseBuilder(this, MovieDB.class, Network.DB_NAME).allowMainThreadQueries().build().getMovieDao();
        ImageView posterBannerIV = findViewById(R.id.movie_poster);
        TextView titleTextView = findViewById(R.id.movie_title);
        TextView releaseTV = findViewById(R.id.release_date);
        TextView ratingTV = findViewById(R.id.rating_value);
        TextView description = findViewById(R.id.synopsis_text);
        scrollView = findViewById(R.id.s);
        favTV = findViewById(R.id.t);
        recyclerViewTr = findViewById(R.id.trailerRV);
        recyclerViewTr.setHasFixedSize(false);
        recyclerViewTr.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerViewTr.setItemAnimator(new DefaultItemAnimator());
        trailerLists = new ArrayList<>();
        recyclerViewRV = findViewById(R.id.reviewsRV);
        recyclerViewRV.setHasFixedSize(false);
        recyclerViewRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewRV.setItemAnimator(new DefaultItemAnimator());
        reviewsLists = new ArrayList<>();
        Bundle data = getIntent().getExtras();
        assert data != null;
        movieList = data.getParcelable(Network.PARCEL_KEY);
        assert movieList != null;
        idVal = movieList.getId();
        final String titleText = movieList.getTitle();
        String image = Network.POSTER_BASE_URL + movieList.getPosterUrl();
        final String descriptionText = movieList.getDescription();
        final String ratings = movieList.getVoteAverage();
        final String releaseDate = movieList.getReleaseDate();
        loadTrailers();
        loadReviews();

        Picasso.get().load(image).into(posterBannerIV);
        titleTextView.setText(titleText);
        ratingTV.setText(ratings);
        releaseTV.setText(releaseDate);
        description.setText(descriptionText);

        favView = findViewById(R.id.favoriteIcon);
        favView.setOnClickListener(this::toggleFavorite);
        if (mMovieDao.getMovieWithId(idVal)) {
            favView.setImageResource(R.drawable.star);
            favTV.setVisibility(View.GONE);
            isFavorite = true;
        }
    }
    private void loadTrailers() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Network.TRAILERS_MOVIES_URL + idVal + Network.TRAILER_URL + Network.API_KEY , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        TrailerList trailerListData = new TrailerList(jo.getString("id"), jo.getString("key"));
                        trailerLists.add(trailerListData);
                    }
                    adapter = new TrailerAdapter(trailerLists, getApplicationContext());
                    recyclerViewTr.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(details_activity.this, "Couldn't process" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void loadReviews() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Network.TRAILERS_MOVIES_URL + idVal + Network.REVIEW_URL + Network.API_KEY , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        ReviewList reviewsListData = new ReviewList(jo.getString("author"),jo.getString("content"));
                        reviewsLists.add(reviewsListData);
                    }
                    adapter2 = new ReviewAdapter(reviewsLists, getApplicationContext());
                    recyclerViewRV.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(details_activity.this, "Couldn't process" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void toggleFavorite(final View v) {
        GlobalAppExecutor.getInstance().diskIO().execute(new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!isFavorite) {
                            try {
                                mMovieDao.insert(movieList);
                            } catch (SQLiteConstraintException e) {

                            }
                            favView.setImageResource(R.drawable.star);
                            favTV.setVisibility(View.GONE);

                            isFavorite = true;
                        } else {
                            mMovieDao.delete(movieList);
                            favTV.setVisibility(View.VISIBLE);
                            favView.setImageResource(R.drawable.star);
                            setResult(RESULT_OK);

                            isFavorite = false;
                            setupViewModel();
                        }
                    }
                });
            }
        });
    }
    private void setupViewModel() {
        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<MovieList>>() {

            @Override
            public void onChanged(@Nullable List<MovieList> taskEntries) {
                new MovieAdapter(taskEntries, getApplicationContext());
            }
        });
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if (position != null) {
            scrollView.postDelayed(new Runnable() {
                public void run() {
                    scrollView.scrollTo(position[0], position[1]);
                }
            }, 100);
        }
    }

}