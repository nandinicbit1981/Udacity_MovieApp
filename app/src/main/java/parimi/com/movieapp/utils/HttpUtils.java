package parimi.com.movieapp.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import parimi.com.movieapp.BuildConfig;

import static parimi.com.movieapp.utils.Constants.BASE_URL;

public class HttpUtils {
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getMoviesBySortPref(String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("api_key", API_KEY);
        client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
    }

    public static void getTrailers(String id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("api_key", API_KEY);
        client.get(getVideoUrl(id), params, responseHandler);
    }


    public static void getReviews(String id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("api_key", API_KEY);
        client.get(getReviewUrl(id), params, responseHandler);
    }

    public static void getMovieById(String id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("api_key", API_KEY);
        client.get(BASE_URL + id, params, responseHandler);
    }



    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String getVideoUrl(String id) {
        return BASE_URL + id + "/videos";
    }

    private static String getReviewUrl(String id) {return BASE_URL + id + "/reviews";}
}