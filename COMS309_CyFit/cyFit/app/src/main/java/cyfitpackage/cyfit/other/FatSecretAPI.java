package cyfitpackage.cyfit.other;

import android.net.Uri;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FatSecretAPI {

    private final static String METHOD = "GET";
    private final static String URL = "http://platform.fatsecret.com/rest/server.api";
    private final static String CONSUMER_KEY = "d1b92abe00ba47b2ab5a9a6d6844ab84";
    private final static String CONSUMER_SECRET = "978fd2b0b7ff498c8031cea2d1870b2d&";
    private final static String SIGNITURE_METHOD = "HMAC-SHA1";
    private final static String FORMAT = "json";
    private final static String VERSION = "1.0";

    public FatSecretAPI() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public JSONArray searchFood(String food) {
        String params = generateParams() +"&method=foods.search" +"&search_expression=" + Uri.encode(food);
        String request = params + "&oauth_signature=" + signRequest(params);
        Log.d("Request", request);
        JSONObject foods = null;
        try {
            URL url = new URL(URL + "?" + request);
            URLConnection connection = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) builder.append(line);
            JSONObject response = new JSONObject(builder.toString());
            Log.d("FatSecret API Search", response.toString());
            foods = response.optJSONObject("foods");
            reader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return (foods == null) ? new JSONArray() : foods.optJSONArray("food");
    }

    public JSONObject getNutritionData(Long ID) {
        String params = generateParams() +"&method=food.get" +"&food_id=" + ID;
        String request = params +"&oauth_signature=" +signRequest(params);
        JSONObject data = null;
        try {
            URL url = new URL(URL + "?" + request);
            URLConnection connection = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null)
                builder.append(line);
            JSONObject response = new JSONObject(builder.toString());
            Log.d("FatSecret API Get", response.toString());
            data = response.optJSONObject("food");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String generateParams() {
        return "oauth_consumer_key=" + CONSUMER_KEY + "&oauth_signature_method=" + SIGNITURE_METHOD + "&oauth_timestamp=" + Long.valueOf(System.currentTimeMillis() / 1000).toString() +
                "&oauth_nonce=" + UUID.randomUUID().toString() +"&oauth_version=" + VERSION + "&format=" + FORMAT;
    }

    private static String signRequest(String params) {
        String[] args = params.split("&");
        Arrays.sort(args);
        String sortedParams = TextUtils.join("&", args);
        String request = METHOD + "&" + Uri.encode(URL) + "&" + Uri.encode(sortedParams);
        SecretKey secret = new SecretKeySpec(CONSUMER_SECRET.getBytes(), SIGNITURE_METHOD);
        try {
            Mac m = Mac.getInstance(SIGNITURE_METHOD);
            m.init(secret);
            return Uri.encode(new String(Base64.encode(m.doFinal(request.getBytes()), Base64.DEFAULT)).trim());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Could Not Encode OAuth Request";
        }
    }
}
