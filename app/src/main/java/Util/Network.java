package Util;

public final class Network {
    public static final String API_KEY = ""; //TODO add api key here
    public static boolean FAV = false;
    public static final String BASE_URL_MOVIE = "https://api.themoviedb.org/3/discover/movie?api_key=";
    public static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String TRAILERS_MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    public static final String PARCEL_KEY = "MovieParcel";
    public static final String TABLE_NAME ="favMovies";
    public static final String DB_NAME = "favMovies.db";
    public static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    public static final String REVIEW_URL = "/reviews?api_key=";
    public static final String TRAILER_URL = "/videos?api_key=";
}