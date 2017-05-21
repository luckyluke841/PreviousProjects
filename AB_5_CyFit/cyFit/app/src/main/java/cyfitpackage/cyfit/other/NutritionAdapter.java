package cyfitpackage.cyfit.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cyfitpackage.cyfit.R;

public class NutritionAdapter extends BaseAdapter {

    private List<Food> foods;
    private static LayoutInflater inflater = null;

    public NutritionAdapter(Context context, List<Food> data) {
        this.foods = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.row_food_result, null);
        TextView name = (TextView) view.findViewById(R.id.result_food_name);
        TextView description = (TextView) view.findViewById(R.id.result_food_description);
        String title = foods.get(position).getBrand() + " " + foods.get(position).getName();
        name.setText(title);
        description.setText(foods.get(position).getDescription());
        return view;
    }
}
