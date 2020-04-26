package Util;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Data.MovieDB;
import Data.MovieList;

public class MovieViewModel extends AndroidViewModel {
    private static final String TAG = MovieViewModel.class.getSimpleName();
    private LiveData<List<MovieList>> tasks;
    public MovieViewModel(Application application) {
        super(application);
        MovieDB database = MovieDB.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.getMovieDao().getMovies();
    }
    public LiveData<List<MovieList>> getTasks() {
        return tasks;
    }
}