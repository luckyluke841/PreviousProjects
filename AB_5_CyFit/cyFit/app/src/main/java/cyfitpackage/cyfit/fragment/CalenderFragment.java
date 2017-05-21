package cyfitpackage.cyfit.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.other.Food;
import cyfitpackage.cyfit.other.FoodAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.Workout;
import cyfitpackage.cyfit.other.WorkoutAdapter;

/**
 * The calender fragment
 */
public class CalenderFragment extends Fragment {
    private TextView nutritionListTitle;
    private TextView workoutListTitle;
    //private TextView musicListTitle;
    private String userID;

    private RequestQueue queue;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private RecyclerView workoutRV;
    private RecyclerView nutritionRV;

    private List<Workout> workouts;
    private List<Food> foods;
    //private List<Music> music;

    /**
     * Instantiates a new Calender fragment.
     */
    public CalenderFragment() {

    }

    /**
     * Factory method to create a new instance of
     * the calender fragment.
     *
     * @return A new instance of fragment CalenderFragment.
     */
    public static CalenderFragment newInstance() {
        CalenderFragment fragment = new CalenderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads previous application instance if present.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = RequestQueueSingleton.getInstance(getContext()).getRequestQueue();
        workouts = new ArrayList<>();
        foods = new ArrayList<>();
        Bundle args = getArguments();
        userID = args.getString("userID");
    }

    /**
     * Sets the calender fragment view for the application.
     *
     * @param inflater view inflater
     * @param container current view context
     * @param savedInstanceState previous session data
     * @return calender fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_calender, container, false);
        workoutRV = (RecyclerView) fl.findViewById(R.id.workoutDateRV);
        nutritionRV = (RecyclerView) fl.findViewById(R.id.nutritionDateRV);
        CalendarView calendar = (CalendarView)fl.findViewById(R.id.calendarView);
        workoutListTitle = (TextView) fl.findViewById(R.id.workoutListTitle);
        //musicListTitle = (TextView) fl.findViewById(R.id.musicListTitle);
        nutritionListTitle = (TextView) fl.findViewById(R.id.nutritionListTitle);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                String date = year + "-" + (month + 1) + "-" + day;
                try {
                    updateContent(sdf.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        LinearLayoutManager llm1 = new LinearLayoutManager(this.getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        workoutRV.setLayoutManager(llm1);
        workoutRV.setAdapter(new WorkoutAdapter(workouts));
        LinearLayoutManager llm2 = new LinearLayoutManager(this.getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        nutritionRV.setLayoutManager(llm2);
        nutritionRV.setAdapter(new FoodAdapter(foods));
        updateContent(new Date());
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

    private void updateContent(Date date) {
        //final String musicTitle = getString(R.string.music_list_title);
        final String workoutTitle = getString(R.string.workout_list_title);
        final String nutritionTitle = getString(R.string.nutrition_list_title);

        final String formattedDate = sdf.format(date);

        workoutListTitle.setText(workoutTitle + " " + formattedDate);
        //musicListTitle.setText(musicTitle + " " + date);
        nutritionListTitle.setText(nutritionTitle + " " + formattedDate);

        updateWorkouts(formattedDate);
        updateNutrition(Long.toString(date.getTime()));
    }

    private void updateWorkouts(final String date) {
        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWorkoutsByDate.php";
        workouts.clear();

        StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONArray workoutData = new JSONArray(response);
                    for (int i = 0; i < workoutData.length(); i++) {
                        workouts.add(new Workout());
                        try {
                            workouts.get(i).setName(workoutData.getJSONObject(i).get("name").toString());
                            workouts.get(i).setDesc(workoutData.getJSONObject(i).get("description").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    workoutRV.setAdapter(new WorkoutAdapter(workouts));
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
                Map<String, String> params = new HashMap<>();
                params.put("calendarDate", date);
                params.put("userID", userID);
                return params;
            }
        };
        queue.add(loginRequest);
    }

    private void updateNutrition(final String date) {
        String url2 = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFoods.php";
        foods.clear();
        StringRequest genListRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("List Response", response);
                try {
                    JSONArray foodList = new JSONArray(response);
                    for (int i = 0; i < foodList.length(); i++) {
                        try {
                            String ID = foodList.getJSONObject(i).get("ID").toString();
                            String name = foodList.getJSONObject(i).get("name").toString();
                            String description = foodList.getJSONObject(i).get("description").toString();
                            String brand = foodList.getJSONObject(i).get("brand").toString();
                            foods.add(new Food(ID, name, brand, description));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    nutritionRV.setAdapter(new FoodAdapter(foods));
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
                params.put("user", userID);
                params.put("date", date);
                return params;
            }
        };
        queue.add(genListRequest);
    }
}
