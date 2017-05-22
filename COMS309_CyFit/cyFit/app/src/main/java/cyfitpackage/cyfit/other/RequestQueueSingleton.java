package cyfitpackage.cyfit.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by gharkness on 2/26/17.
 */
public class RequestQueueSingleton {

    private static RequestQueueSingleton ourInstance;

    private RequestQueue requestQueue;

    private ImageLoader imageLoader;

    private static Context context;

    private RequestQueueSingleton(Context context) {
        this.context = context;

        this.requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(this.requestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RequestQueueSingleton(context);
        }
        return ourInstance;
    }

    public RequestQueue getRequestQueue() {
        if (this.requestQueue ==null) {
            requestQueue = Volley.newRequestQueue(this.context.getApplicationContext());
        }

        return this.requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        this.getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

}
