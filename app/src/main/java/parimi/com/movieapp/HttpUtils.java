package parimi.com.movieapp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "";//TODO replace with correct API;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getImage(String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("api_key", API_KEY);
        client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}