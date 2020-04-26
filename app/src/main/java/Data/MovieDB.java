package Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import Util.Network;

@Database(entities = { MovieList.class }, version = 3, exportSchema = false)
public abstract class MovieDB extends RoomDatabase {
    public abstract MovieDao getMovieDao();
    private static MovieDB INSTANCE;
    public static MovieDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDB.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MovieDB.class, Network.DB_NAME).build();
            }
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
}