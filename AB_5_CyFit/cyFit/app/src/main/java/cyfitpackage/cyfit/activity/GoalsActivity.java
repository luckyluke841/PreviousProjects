package cyfitpackage.cyfit.activity;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.solver.Goal;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.fragment.DeleteGoalFragment;
import cyfitpackage.cyfit.other.GoalAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.WorkoutGoal;

public class GoalsActivity extends AppCompatActivity{

    private List<WorkoutGoal> myGoals = new ArrayList<WorkoutGoal>();
    public RecyclerView goalView;
    private static RequestQueue queue;
    private String userID;
    DeleteGoalFragment deleteGoalFragment;
    FragmentManager fm;
    FrameLayout frameLayout;

    @Override
    /**
     * Create Activity for viewing, adding, updating, and deleting goals
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        userID = getIntent().getStringExtra("USER_ID");

        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        frameLayout = (FrameLayout) findViewById(R.id.goalFrameLayout);
        goalView = (RecyclerView) frameLayout.findViewById(R.id.goalrv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        goalView.setLayoutManager(llm);

        FloatingActionButton fab = (FloatingActionButton) frameLayout.findViewById(R.id.fab);
        final Intent addNewGoalIntent = new Intent(this, AddGoalActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewGoalIntent.putExtra("USER_ID", userID);
                startActivity(addNewGoalIntent);
                finish();
            }
        });


        getGoals();
        goalView.setAdapter(new GoalAdapter(this, myGoals, userID));



    }

    private void getGoals(){
        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetGoals.php";
        final GoalsActivity activity = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray goalData = new JSONArray(response);

                    for (int i = 0; i < goalData.length(); i++) {
                        WorkoutGoal currentGoal = new WorkoutGoal();
                        currentGoal.setGoalId(Integer.valueOf(goalData.getJSONObject(i).get("goalID").toString()));
                        currentGoal.setName(goalData.getJSONObject(i).get("goalName").toString());
                        currentGoal.setDescription(goalData.getJSONObject(i).get("description").toString());
                        currentGoal.setComplete(Integer.valueOf(goalData.getJSONObject(i).get("complete").toString()));
                        myGoals.add(currentGoal);
                    }
                    goalView.setAdapter(new GoalAdapter(activity, myGoals, userID));
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

    public void updateComplete(String goalNumber, boolean isChecked, String userID){
        final boolean completeIsChecked = isChecked;
        final String goalNum = goalNumber;
        final String myUserID = userID;
        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/UpdateCompletedGoals.php";
        try{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                public void onResponse(String response) {
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
                    params.put("userID", myUserID);
                    params.put("goalID", goalNum.substring(0,1));

                    if(completeIsChecked){
                        params.put("complete", "1");
                    }
                    else{
                        params.put("complete", "0");
                    }
                    return params;
                }
            };

            queue.add(stringRequest);
        } catch(NullPointerException e){

        }

    }

    public void showDeleteFragment(String userID, String goalID){
        final GoalsActivity activity = this;
        fm = getFragmentManager();
        deleteGoalFragment = DeleteGoalFragment.newInstance(userID, goalID);
        deleteGoalFragment.setActivity(activity);
        deleteGoalFragment.show(fm, "DeleteGoal");
    }

    public void notifyOnDeletedGoal(Activity activity){
        Context context = activity.getApplicationContext();
        myGoals = new ArrayList<WorkoutGoal>();
        getGoals();
        CharSequence text = "Deleted Goal";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
