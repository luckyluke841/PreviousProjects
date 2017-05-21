package cyfitpackage.cyfit.other;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.GoalsActivity;

import static cyfitpackage.cyfit.R.string.goals;


/**
 * Created by victordasilva on 3/24/17.
 */

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<WorkoutGoal> workoutGoalList;
    private static String userID;
    private static RequestQueue queue;
    private GoalsActivity activity;


    //private static RequestQueue queue = RequestQueueSingleton.getInstance();


    public GoalAdapter(GoalsActivity activity, List<WorkoutGoal> workoutGoalList, String userID) {
        this.activity = activity;
        this.workoutGoalList = workoutGoalList;
        this.userID=userID;
    }

    public GoalAdapter(List<WorkoutGoal> workoutGoalList, String userID) {
        this.workoutGoalList = workoutGoalList;
        this.userID=userID;
    }


    public int getItemCount() {
        return workoutGoalList.size();
    }




    public void onBindViewHolder(GoalViewHolder contactViewHolder, int i) {
        WorkoutGoal workoutGoal = workoutGoalList.get(i);
        contactViewHolder.goalNum.setText(workoutGoal.getGoalId() + ". ");
        contactViewHolder.goalName.setText(workoutGoal.getName());
        contactViewHolder.goalDescription.setText(workoutGoal.getDescription());

        //Goal Completed
        if(workoutGoal.getComplete() == 1){
            contactViewHolder.complete.setChecked(true);
        }
        //Goal Not Completed
        else{
            contactViewHolder.complete.setChecked(false);
        }
    }


    public GoalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.goal_card, viewGroup, false);
        return new GoalViewHolder(itemView);
    }


    class GoalViewHolder extends RecyclerView.ViewHolder {

        /**
         * The Goal Number
         */
        TextView goalNum;
        /**
         * The goal description
         */

        /**
         * The Goal Name
         */
        TextView goalName;

        /**
         *  The Goal Description
         */
        TextView goalDescription;
        //protected ImageView vPicture;

        /**
         * The Completed checkbox indicator
         */
        CheckBox complete;


        /**
         * Instantiates a new ViewHolder for a workout goal object
         *
         * @param v the specified view
         */
        GoalViewHolder(View v) {
            super(v);
            goalNum = (TextView) v.findViewById(R.id.goal_number);
            goalName = (TextView) v.findViewById(R.id.goal_name);
            goalDescription = (TextView)  v.findViewById(R.id.goal_description);
            complete = (CheckBox) v.findViewById(R.id.goalCompleteCheckBox);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.showDeleteFragment(userID, goalNum.getText().toString());
                }
            });

            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoalsActivity goalsActivity = new GoalsActivity();
                    goalsActivity.updateComplete(goalNum.getText().toString(), complete.isChecked(), userID);

                }
            });

        }
    }
}
