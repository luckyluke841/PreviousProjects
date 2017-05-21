package cyfitpackage.cyfit.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.Workout;
import cyfitpackage.cyfit.other.WorkoutAdapter;

/**
 * The Workout Fragment
 */
public class WorkoutFragment extends Fragment {

    //private RequestQueue queue;
    private List<Workout> workouts;
    private RecyclerView recView;

    private String userID;

    /**
     * Instantiates a new Workout fragment.
     */
    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * workout fragment.
     *
     * @return A new instance of fragment WorkoutFragment.
     */
    public static WorkoutFragment newInstance() {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads previous application instance if present
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        workouts = new ArrayList<>();
        userID = args.getString("userID");
    }

    /**
     * Sets the workout fragment view for the application.
     *
     * @param inflater view inflater
     * @param container current view context
     * @param savedInstanceState previous session data
     * @return workout fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_workout, container, false);
        //init recycler view with cards
        recView = (RecyclerView) fl.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);
        recView.setAdapter(new WorkoutAdapter(workouts));
        FloatingActionButton fab = (FloatingActionButton) fl.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cyfitpackage.cyfit.activity.AddWorkoutActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });
        getWorkouts();
        return fl;
    }

    /**
     * Sets current context for fragment.
     *
     * @param context current application context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Default actions for fragment detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This method makes a Volley call to the server for previously saved workouts and
     * then updates the RecyclerView with these saved workouts.
     */
    private void getWorkouts() {

        workouts.clear();
        RequestQueue queue = RequestQueueSingleton.getInstance(getContext()).getRequestQueue();

        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWorkoutsForBrowsing.php";

        StringRequest getWorkoutsRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONArray workoutData = new JSONArray(response);
                    for (int i = 0; i < workoutData.length(); i++) {
                        workouts.add(new Workout());
                        try {
                            workouts.get(i).setName(workoutData.getJSONObject(i).get("workoutName").toString());
                            workouts.get(i).setDesc(workoutData.getJSONObject(i).get("description").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    recView.setAdapter(new WorkoutAdapter(workouts));
                } catch (JSONException e) {
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

        queue.add(getWorkoutsRequest);
    }
}
