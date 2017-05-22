package cyfitpackage.cyfit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * The type Add nutrition activity.
 */
public class AddNutritionActivity extends AppCompatActivity {

    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrition);

        final EditText nutritionType = (EditText) findViewById(R.id.AddNutritionType);
        final EditText AddNutritionDescription = (EditText) findViewById(R.id.AddNutritionDescription);
        final EditText AddNutritionCalories = (EditText) findViewById(R.id.AddNutritionCalories);
        final TextView successIndicator = (TextView) findViewById(R.id.NutritionSuccessIndicator);
        final Button addNutritionButton = (Button) findViewById(R.id.AddNutritionButton);
        final RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/CreateNutrition.php";

        addNutritionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        successIndicator.setText("Success!");
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        String nutritionName = nutritionType.getText().toString();
                        String nutritionDescription = AddNutritionDescription.getText().toString();
                        String nutritionCalories = AddNutritionCalories.getText().toString();
                        params.put("nutritionName", nutritionName);
                        params.put("nutritionDescription", nutritionDescription);
                        params.put("nutritionCalories", nutritionCalories);
                        return params;
                    };
                };
                queue.add(stringRequest);
            }
        });
    }
}

