package cyfitpackage.cyfit.other;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by skywa on 2/20/2017.
 */
public class VolleyMethods
{
    private static final String TAG = "VolleyMethods";
    private static VolleyMethods instance = null;

    private String thisRequestSuffix = "sendNutritionString";//TODO create a PHP file "proj-309-ab-5.cs.iastate.edu/sendNutritionString.php" with proper params..... I think.

    private static final String prefixURL = "proj-309-ab-5.cs.iastate.edu/";

    /**
     * The Request queue.
     */
//for Volley API
    public RequestQueue requestQueue;

    /**
     * starts a requestQueue
     * @param context
     */
    private VolleyMethods(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //other stuff if you need
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static synchronized VolleyMethods getInstance(Context context)
    {
        if (null == instance)
            instance = new VolleyMethods(context);
        return instance;
    }

/**
     * Gets instance.
     *
     * @return the instance
     */
//this is so you don't need to pass context each time
    public static synchronized VolleyMethods getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(VolleyMethods.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    /**
     * volley method using POST to send a string to the database
     * Some post request returning string.
     *
     * @param param1   the param 1
     * @param listener the listener
     */
    public void somePostRequestReturningString(Object param1, final SomeCustomListener<String> listener)
    {

        String url = prefixURL + thisRequestSuffix;

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("param1", param1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "somePostRequest Response : " + response.toString());
                        if(null != response.toString())
                            listener.getResult(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult("false");//
                        }
                    }
                });

        requestQueue.add(request);
    }

    /**
     * volley method using GET to return a string from the database through the server
     * @param url
     * @param params
     * @return
     */
    private String createGetWithParams(String url, Map<String, Object> params)
    {
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet())
        {
            Object value = params.get(key);
            if (value != null)
            {
                try
                {
                    value = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.toString());//changed min SDK from 15 to 19, also hope that this works  TODO make this work lol
                    if (builder.length() > 0)
                        builder.append("&");
                    builder.append(key).append("=").append(value);
                }
                catch (UnsupportedEncodingException e)
                {
                }
            }
        }

        return (url += "?" + builder.toString());
    }

    /**
     * The Requested string.
     */
    String requestedString;




    /**
     * this method sits somewhere in the same class, this fires the request
     * String request string.
     *
     * @param someRequestURL     the some request url
     * @param someParameter      the some parameter
     * @param someParameterValue the some parameter value
     * @return the string
     */
//this method sits somewhere in the same class, this fires the request

    public String stringRequest(String someRequestURL, String someParameter, String someParameterValue) {

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put(someParameter, someParameterValue);


        String url = createGetWithParams(someRequestURL, jsonParams);

        final StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestedString = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse) {
                            Log.d("Volley Error: ", "" + error.networkResponse.statusCode);
                        }
                    }
                });

        requestQueue.add(request);
        return requestedString;
    }


    /**
     * returns the result in the form of a listener
     * @param <T> the type parameter
     */
    public interface SomeCustomListener<T>
    {
        /**
         * Gets result.
         *
         * @param object the object
         */
        public void getResult(T object);
    }
}
