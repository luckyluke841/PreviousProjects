package cyfitpackage.cyfit.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;

/**
 * The type Add workout activity.
 */
public class AddWorkoutActivity extends AppCompatActivity {

    //private GoogleApiClient client;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);


        userID = getIntent().getStringExtra("USER_ID");

        final EditText workoutType = (EditText) findViewById(R.id.AddWorkoutType);

        final EditText AddWorkoutDescription = (EditText) findViewById(R.id.AddWorkoutDescription);
        final TextView successIndicator = (TextView) findViewById(R.id.SuccessIndicator);
        final Button addWorkoutButton = (Button) findViewById(R.id.AddWorkoutButton);
        final RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/CreateWorkout.php";

        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringRequest addWorkoutRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*try {
                            Log.d("Response", response);
                            JSONObject loginInfo = new JSONObject(response);
                            Log.d("Server", loginInfo.getString("Message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        successIndicator.setText("Success!");
                        finish();
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
                        String workoutName = workoutType.getText().toString();
                        String workoutDescription = AddWorkoutDescription.getText().toString();
                        params.put("workoutName", workoutName);
                        params.put("workoutDescription", workoutDescription);
                        params.put("userID", userID);
                        return params;
                    }
                };
                queue.add(addWorkoutRequest);
            }
        });
    }
}
