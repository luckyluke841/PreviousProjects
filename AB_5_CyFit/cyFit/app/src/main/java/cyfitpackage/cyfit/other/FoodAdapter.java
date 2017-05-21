package cyfitpackage.cyfit.other;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cyfitpackage.cyfit.R;

/**
 * Adapter to implement CardView and RecyclerView for Workout fragment
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foodList;

    /**
     * Instantiates a new Workout adapter.
     *
     * @param foodList list of workouts to generate CardView for
     */
    public FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    /**
     * Gets the size of the given workouts list
     *
     * @return size of workouts list
     */
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    /**
     * Sets the workout information for RecyclerView ViewHolder
     *
     * @param contactViewHolder workout ViewHolder
     * @param i index of Workout in workouts array
     */
    @Override
    public void onBindViewHolder(FoodViewHolder contactViewHolder, int i) {
        Food food = foodList.get(i);
        contactViewHolder.vName.setText(food.getName());
        contactViewHolder.vDescription.setText(food.getDescription());
    }

    /**
     *
     * @param viewGroup current application view
     * @param i generic implementation for override
     * @return new ViewHolder instance for Workout CardView
     */
    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.food_card, viewGroup, false);
        return new FoodViewHolder(itemView);
    }

    /**
     * RecyclerView ViewHolder implementation for workout object.
     */
    static class FoodViewHolder extends RecyclerView.ViewHolder {

        /**
         * The food name
         */
        TextView vName;
        /**
         * The food description
         */
        TextView vDescription;
        //protected ImageView vPicture;

        /**
         * Instantiates a new ViewHolder for a workout object
         *
         * @param v the specified view
         */
        FoodViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.food_name);
            vDescription = (TextView)  v.findViewById(R.id.food_description);
        }
    }
}
