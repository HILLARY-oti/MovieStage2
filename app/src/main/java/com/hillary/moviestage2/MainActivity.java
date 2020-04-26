package com.hillary.moviestage2;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.MovieAdapter;
import Data.MovieDB;
import Data.MovieDao;
import Data.MovieList;
import Util.MovieViewModel;

import static Util.Network.API_KEY;
import static Util.Network.BASE_URL_MOVIE;
import static Util.Network.DB_NAME;
import static Util.Network.FAV;
import static Util.Network.PARCEL_KEY;
import static Util.Network.POPULAR_MOVIES_URL;
import static Util.Network.TOP_RATED_MOVIES_URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String LOG_TAG = "MainActivity";
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<MovieList> movieLists;
    private MovieDao mMovieDao;
    private Button mRefreshButton;
    private ProgressBar mNetworkLoadProgressBar;
    private TextView mErrorTextView;
    private Parcelable listState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshButton = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.movies_list);
        mNetworkLoadProgressBar = findViewById(R.id.progress_bar);
        mErrorTextView = findViewById(R.id.error_text);
        recyclerView.setHasFixedSize(true);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        movieLists = new ArrayList<>();
        mMovieDao = Room.databaseBuilder(this, MovieDB.class, DB_NAME).allowMainThreadQueries().build().getMovieDao();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PARCEL_KEY)) {
                movieLists = savedInstanceState.getParcelableArrayList(PARCEL_KEY);
                adapter = new MovieAdapter(movieLists, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
        else {
            if (FAV)
                loadFavMovies();
            else
                loadUrlData(BASE_URL_MOVIE);
        }
        mRefreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadFavMovies();
            }
        });
    }
    public void loadFavMovies() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRefreshButton.setVisibility(View.INVISIBLE);
        LiveData<List<MovieList>> movieListsL = mMovieDao.getMovies();
        movieListsL.observe(this, new Observer<List<MovieList>>() {

            @Override
            public void onChanged(@Nullable List<MovieList> movieLists) {
                adapter.setMoviesLive(movieLists);
                adapter = new MovieAdapter(adapter.getMoviesLive(), getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void loadUrlData(String movieUrl) {
        mNetworkLoadProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,movieUrl + API_KEY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        MovieList movieList = new MovieList(jo.getLong("id"), jo.getString("title"), jo.getString("overview"), jo.getString("poster_path"), jo.getString("vote_average"), jo.getString("release_date"));
                        movieLists.add(movieList);
                    }
                    adapter = new MovieAdapter(movieLists, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                mNetworkLoadProgressBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mErrorTextView.setVisibility(View.VISIBLE);
                mRefreshButton.setVisibility(View.VISIBLE);

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PARCEL_KEY, (ArrayList<? extends Parcelable>) movieLists);
        listState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state != null)
            listState = state.getParcelable(KEY_RECYCLER_STATE);
    }
    private void setupViewModel() {
        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<MovieList>>() {

            @Override
            public void onChanged(@Nullable List<MovieList> taskEntries) {
                adapter.setMoviesLive(taskEntries);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.popular:
                item.setChecked(true);
                movieLists.clear();
                FAV = false;
                loadUrlData(POPULAR_MOVIES_URL);
                return true;
            case R.id.rated:
                item.setChecked(true);
                movieLists.clear();
                FAV = false;
                loadUrlData(TOP_RATED_MOVIES_URL);
                return true;
            case R.id.fav:
                item.setChecked(true);
                movieLists.clear();
                FAV = true;
                loadFavMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (listState != null)
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (FAV) {
            loadFavMovies();
        }
    }

    @Override
    public void onClickItem(double movie) {

    }
}
