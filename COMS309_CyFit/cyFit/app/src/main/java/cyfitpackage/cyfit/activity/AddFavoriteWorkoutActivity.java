package cyfitpackage.cyfit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.Workout;

public class AddFavoriteWorkoutActivity extends AppCompatActivity {

    private EditText name, description;
    private Button addButton;
    private TextView successIndicator;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.AddFavoriteWorkoutName);
        description = (EditText) findViewById(R.id.AddFavoriteWorkoutDescription);
        addButton = (Button) findViewById(R.id.AddFavoriteWorkoutButton);
        successIndicator = (TextView) findViewById(R.id.FavoriteWorkoutSuccessIndicator);
        userID = getIntent().getStringExtra("USER_ID");

        final RequestQueue queue = Volley.newRequestQueue(this);



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFavoriteWorkouts.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONArray favWorkoutsArray = new JSONArray(response);

                            final int numFavoriteWorkouts = favWorkoutsArray.length();

                            if(numFavoriteWorkouts > 2){
                                successIndicator.setText("Max Fav Workouts = 3");
                            }
                            else{

                                String addURL = "http://proj-309-ab-5.cs.iastate.edu/Client/AddFavoriteWorkout.php";
                                StringRequest addRequest = new StringRequest(Request.Method.POST, addURL, new Response.Listener<String>() {
                                    public void onResponse(String response) {
                                        successIndicator.setText("Success");
                                        goBackToFavoriteWorkouts();

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Volley", error.toString());
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("userID", userID);
                                        params.put("name", name.getText().toString());
                                        params.put("description", description.getText().toString());
                                        return params;
                                    }
                                };

                                queue.add(addRequest);

                            }



                        }catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userID", userID);
                        return params;
                    }
                };

                queue.add(stringRequest);

            }
        });

    }

    private void goBackToFavoriteWorkouts(){
        Intent backToFavoriteWorkouts = new Intent(this, cyfitpackage.cyfit.activity.FavoriteWorkoutsActivity.class);
        backToFavoriteWorkouts.putExtra("USER_ID", userID);
        startActivity(backToFavoriteWorkouts);
        finish();
    }

}