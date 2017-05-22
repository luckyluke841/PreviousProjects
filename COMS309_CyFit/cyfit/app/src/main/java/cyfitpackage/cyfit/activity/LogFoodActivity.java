package cyfitpackage.cyfit.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.other.FatSecretAPI;
import cyfitpackage.cyfit.other.Food;
import cyfitpackage.cyfit.other.NutritionAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;

public class LogFoodActivity extends AppCompatActivity {

    private FatSecretAPI api;
    private android.support.v7.widget.SearchView searchView;
    private ListView foodList;
    private String date;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food);

        date = getIntent().getStringExtra("DATE");
        user = getIntent().getStringExtra("USER");

        api = new FatSecretAPI();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.food_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        foodList = (ListView) findViewById(R.id.food_search_results);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Food selected = (Food) foodList.getAdapter().getItem(position);

                final NumberPicker numberPicker = new NumberPicker(LogFoodActivity.this);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(20);
                numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogFoodActivity.this);
                alertDialogBuilder.setMessage(selected.getName() + "\n\nNumber of Servings:");
                        alertDialogBuilder.setPositiveButton("Add Food",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            Object data = selected.getDetailedData();
                                            JSONObject foodData = (data instanceof JSONArray) ? ((JSONArray)data).getJSONObject(0) : ((JSONObject)data);
                                            Log.d("Food Data: ", foodData.toString());
                                            String servings = Integer.toString(numberPicker.getValue());
                                            String calories = foodData.getString("calories");
                                            String carbohydrates = foodData.getString("carbohydrate");
                                            String protein = foodData.getString("protein");
                                            String fat = foodData.getString("fat");
                                            logFood(selected.getID(), selected.getName(), selected.getBrand(), calories, carbohydrates, protein, fat, servings);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
                alertDialogBuilder.setView(numberPicker);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        Button btnFinish = (Button) findViewById(R.id.btnFinishLogFood);
        assert btnFinish != null;
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFood(query);
        }
    }

    private void searchFood(String input) {
        JSONArray results = api.searchFood(input);
        Log.d("Search Results", results.toString());
        ArrayList<Food> foods = new ArrayList<>();
        try {
            for (int i = 0; i < results.length(); i++) {
                JSONObject food = results.getJSONObject(i);
                String brand = (food.getString("food_type").equals("Generic")) ? "Generic" : food.getString("brand_name");
                String name = food.getString("food_name");
                String id = food.getString("food_id");
                String description = food.getString("food_description");
                foods.add(new Food(id, name, brand, description));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        foodList.setAdapter(new NutritionAdapter(this, foods));
        searchView.clearFocus();
    }

    private void logFood(final Long ID, final String name, final String brand, final String calories, final String carbohydrates, final String protein, final String fat, final String servings) {
        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/LogFood.php";
        final RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest logFoodRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", ID.toString());
                params.put("name", name);
                params.put("brand", brand);
                params.put("calories", calories);
                params.put("carbohydrates", carbohydrates);
                params.put("protein", protein);
                params.put("fat", fat);
                params.put("servings", servings);
                params.put("date", date);
                params.put("user", user);
                return params;
            }
        };
        queue.add(logFoodRequest);
    }
}
