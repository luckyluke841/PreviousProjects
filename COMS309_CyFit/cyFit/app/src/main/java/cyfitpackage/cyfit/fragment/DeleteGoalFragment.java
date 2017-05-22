package cyfitpackage.cyfit.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.GoalsActivity;
import cyfitpackage.cyfit.other.GoalAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.WorkoutGoal;

import static cyfitpackage.cyfit.R.string.goals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link} interface
 * to handle interaction events.
 * Use the {@link DeleteGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteGoalFragment extends android.app.DialogFragment {
    int goalID;
    String userID;
    private Button yesButton, noButton;
    private GoalsActivity activity;


    public DeleteGoalFragment(){

    }

    public void setActivity(GoalsActivity activity){
        this.activity=activity;
    }
    /**
     * Create a new instance of DeleteGoalFragment, providing "userID" and "goalID"
     * as an argument.
     */
    public static DeleteGoalFragment newInstance(String userID, String goalID) {
        DeleteGoalFragment f = new DeleteGoalFragment();

        Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putString("goalID", goalID);
        f.setArguments(args);

        return f;
    }

    public void setUserID(String userID){
        this.userID=userID;
    }
    public void setGoalID(int goalID){
        this.goalID=goalID;
    }

    public Button getYesButton(){
        return yesButton;
    }
    public Button getNoButton(){
        return noButton;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getString("userID");
        goalID = Integer.valueOf(getArguments().getString("goalID").substring(0,1));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_goal, container, false);
        getDialog().setTitle("Delete Goal");

        yesButton = (Button) v.findViewById(R.id.YesOptionDeleteGoal);
        noButton = (Button) v.findViewById(R.id.NoOptionDeleteGoal);

        final DeleteGoalFragment thisFrag = this;

        final RequestQueue queue = RequestQueueSingleton.getInstance(getContext()).getRequestQueue();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked Yes");
                String url="http://proj-309-ab-5.cs.iastate.edu/Client/DeleteGoal.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        System.out.println("Delete Response");
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(thisFrag);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        ft.commit();
                        activity.notifyOnDeletedGoal(activity);
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
                        params.put("goalID", String.valueOf(goalID));
                        return params;
                    }
                };

                queue.add(stringRequest);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(thisFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });
        return v;
    }


}