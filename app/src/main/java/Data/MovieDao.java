package Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert
    void insert(MovieList movie);

    @Update
    void update(MovieList... repos);

    @Delete
    void delete(MovieList movie);

    @Query("SELECT * FROM  moviesFav")
    LiveData<List<MovieList>> getMovies();

    @Query("SELECT * FROM moviesFav WHERE id = :number")
    boolean getMovieWithId(long number);
}