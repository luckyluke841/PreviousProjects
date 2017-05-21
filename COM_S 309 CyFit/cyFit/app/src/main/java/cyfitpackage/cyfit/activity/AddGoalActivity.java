package cyfitpackage.cyfit.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.fragment.WorkoutFragment;

import static cyfitpackage.cyfit.R.id.AddWorkoutDescription;

/**
 * The type Add workout activity.
 */
public class AddGoalActivity extends AppCompatActivity {

    //private GoogleApiClient client;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        userID = getIntent().getStringExtra("USER_ID");

        final EditText goalName = (EditText) findViewById(R.id.AddGoalName);
        final EditText goalDescription = (EditText) findViewById(R.id.AddGoalDescription);
        final TextView goalSuccessIndicator = (TextView) findViewById(R.id.GoalSuccessIndicator);
        final Button addGoalButton = (Button) findViewById(R.id.AddGoalButton);
        final RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/CreateGoal.php";
        final String getTotGoalsURL = "http://proj-309-ab-5.cs.iastate.edu/Client/getTotalNumGoals.php";

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringRequest getTotalNumGoals = new StringRequest(Request.Method.POST, getTotGoalsURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray numGoalsData = new JSONArray(response);


                            int userGoalID=Integer.valueOf(numGoalsData.getJSONObject(0).get("totalNumGoals").toString()) + 1;
                            final String userGoalIDString = String.valueOf(userGoalID);



                            StringRequest addGoalRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println("Got Second Response");
                                    goalSuccessIndicator.setText("Success!");
                                    goBackToGoals();

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
                                    String goalNameString = goalName.getText().toString();
                                    String goalDescriptionString = goalDescription.getText().toString();
                                    params.put("goalName", goalNameString);
                                    params.put("description", goalDescriptionString);
                                    params.put("userID", userID);
                                    params.put("goalID", userGoalIDString);
                                    return params;
                                }
                            };
                            queue.add(addGoalRequest);




                            }catch(JSONException e){

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
                queue.add(getTotalNumGoals);


            }
        });
    }

    public void goBackToGoals(){
        Intent backToGoals = new Intent(this, cyfitpackage.cyfit.activity.GoalsActivity.class);
        backToGoals.putExtra("USER_ID", userID);
        startActivity(backToGoals);
        finish();
    }
}
