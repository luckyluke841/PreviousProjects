package cyfitpackage.cyfit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.other.GoalAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.Workout;
import cyfitpackage.cyfit.other.WorkoutAdapter;
import cyfitpackage.cyfit.other.WorkoutGoal;

import static cyfitpackage.cyfit.R.id.cv;

public class FavoriteWorkoutsActivity extends AppCompatActivity {


    private List<Workout> favWorkouts;
    private RecyclerView recView;

    private String userID;
    private TextView name1, name2, name3;
    private TextView description1, description2, description3;
    private CardView card1, card2, card3;

    @Override
    /**
     * Create the activity for searching for and obseriving favorite Workouts
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = getIntent().getStringExtra("USER_ID");

        card1 = (CardView) findViewById(cv);
        card2 = (CardView) findViewById(R.id.cv2);
        card3 = (CardView) findViewById(R.id.cv3);


        name1 = (TextView) findViewById(R.id.workout_name1);
        name2 = (TextView) findViewById(R.id.workout_name2);
        name3 = (TextView) findViewById(R.id.workout_name3);
        description1 = (TextView) findViewById(R.id.workout_description1);
        description2 = (TextView) findViewById(R.id.workout_description2);
        description3 = (TextView) findViewById(R.id.workout_description3);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.favefab);
        final Intent addNewFavoriteWorkoutIntent = new Intent(this, AddFavoriteWorkoutActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFavoriteWorkoutIntent.putExtra("USER_ID", userID);
                startActivity(addNewFavoriteWorkoutIntent);
                finish();
            }
        });

        getFavoriteWorkouts();



    }

    private void getFavoriteWorkouts() {

        RequestQueue queue = RequestQueueSingleton.getInstance(getBaseContext()).getRequestQueue();
        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFavoriteWorkouts.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray favWorkoutsArray = new JSONArray(response);

                    for (int i = 0; i < favWorkoutsArray.length(); i++) {
                        if(i==0){
                            name1.setText(favWorkoutsArray.getJSONObject(0).getString("name"));
                            description1.setText(favWorkoutsArray.getJSONObject(0).getString("description"));
                        }
                        else if(i==1){
                            name2.setText(favWorkoutsArray.getJSONObject(1).getString("name"));
                            description2.setText(favWorkoutsArray.getJSONObject(1).getString("description"));
                        }
                        else if(i==2){
                            name3.setText(favWorkoutsArray.getJSONObject(2).getString("name"));
                            description3.setText(favWorkoutsArray.getJSONObject(2).getString("description"));
                        }
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

}
