package cyfitpackage.cyfit.other;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cyfitpackage.cyfit.R;

/**
 * Adapter to implement CardView and RecyclerView for Workout fragment
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workoutList;

    /**
     * Instantiates a new Workout adapter.
     *
     * @param workoutList list of workouts to generate CardView for
     */
    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    /**
     * Gets the size of the given workouts list
     *
     * @return size of workouts list
     */
    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    /**
     * Sets the workout information for RecyclerView ViewHolder
     *
     * @param contactViewHolder workout ViewHolder
     * @param i index of Workout in workouts array
     */
    @Override
    public void onBindViewHolder(WorkoutViewHolder contactViewHolder, int i) {
        Workout workout = workoutList.get(i);
        contactViewHolder.vName.setText(workout.getName());
        contactViewHolder.vDescription.setText(workout.getDesc());
        //contactViewHolder.vPicture.setImageDrawable(workout.picture);
    }

    /**
     *
     * @param viewGroup current application view
     * @param i generic implementation for override
     * @return new ViewHolder instance for Workout CardView
     */
    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.workout_card, viewGroup, false);
        return new WorkoutViewHolder(itemView);
    }

    /**
     * RecyclerView ViewHolder implementation for workout object.
     */
    static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        /**
         * The workout name
         */
        TextView vName;
        /**
         * The workout description
         */
        TextView vDescription;
        //protected ImageView vPicture;

        /**
         * Instantiates a new ViewHolder for a workout object
         *
         * @param v the specified view
         */
        WorkoutViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.workout_name);
            vDescription = (TextView)  v.findViewById(R.id.workout_description);
            //vPicture = (ImageView)  v.findViewById(R.id.workout_photo);
        }
    }
}
