package cyfitpackage.cyfit.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.other.GoalAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.WorkoutGoal;

import static android.R.attr.format;

public class MyInfo extends AppCompatActivity {

    private TextView firstName, lastName, email, height, weight, age, numWorkoutsLogged, creationDate;
    private EditText firstNameEdit, lastNameEdit, emailEdit, heightEditFeet, heightEditInches, weightEdit, ageEdit;
    private Button editInfoButton, cancelChangesButton, submitChangesButton;
    private TextView weightEditUnits, heightFeetUnits, heightInchesUnits, ageYearsUnits;

    private String userID;
    private RequestQueue queue;
    private boolean isWHGiven;

    private String feetString, inchesString, actualWeight, actualAge;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View
        firstName = (TextView) findViewById(R.id.FirstName);
        lastName = (TextView) findViewById(R.id.LastName);
        email = (TextView) findViewById(R.id.Email);
        height = (TextView) findViewById(R.id.Height);
        weight = (TextView) findViewById(R.id.Weight);
        age = (TextView) findViewById(R.id.Age);
        numWorkoutsLogged = (TextView) findViewById(R.id.NumWorkoutsLogged);
        creationDate = (TextView) findViewById(R.id.AccountCreationDate);
        editInfoButton = (Button) findViewById(R.id.EditInfoButton);

        //Edit
        firstNameEdit = (EditText) findViewById(R.id.FirstNameEdit);
        lastNameEdit = (EditText) findViewById(R.id.LastNameEdit);
        emailEdit = (EditText) findViewById(R.id.EmailEdit);
        heightEditFeet = (EditText) findViewById(R.id.HeightFeetEdit);
        heightEditInches = (EditText) findViewById(R.id.HeightInchesEdit);
        heightFeetUnits = (TextView) findViewById(R.id.HeightFeetUnit);
        heightInchesUnits = (TextView) findViewById(R.id.HeightInchesUnit);
        weightEdit = (EditText) findViewById(R.id.WeightEdit);
        weightEditUnits = (TextView) findViewById(R.id.WeightEditUnits);
        ageEdit = (EditText) findViewById(R.id.AgeEdit);
        ageYearsUnits = (TextView) findViewById(R.id.AgeEditUnits);
        cancelChangesButton = (Button) findViewById(R.id.CancelInfoChangeButton);
        submitChangesButton = (Button) findViewById(R.id.SubmitInfoChangesButton);


        editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateScreen();
            }
        });


        cancelChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewingScreen();
            }
        });


        submitChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });


        userID = getIntent().getStringExtra("USER_ID");
        loadUserInfo();

    }


    private void loadUserInfo(){
        queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String getUserInfoURL = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByID.php";

        String getWorkoutInfoURL = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWorkoutsForBrowsing.php";

        String getWeightHeightURL = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWeightAndHeight.php";


        StringRequest userInfoRequest = new StringRequest(Request.Method.POST, getUserInfoURL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject userInfoObject = new JSONObject(response);

                    firstName.setText(userInfoObject.getString("firstname"));
                    lastName.setText(userInfoObject.getString("lastname"));
                    email.setText(userInfoObject.getString("email"));

                    creationDate.setText(userInfoObject.getString("created_at").substring(0,11));
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
        queue.add(userInfoRequest);


        StringRequest workoutInfoRequest = new StringRequest(Request.Method.POST, getWorkoutInfoURL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray workoutInfoArray = new JSONArray(response);
                    numWorkoutsLogged.setText(String.valueOf(workoutInfoArray.length()));

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
        queue.add(workoutInfoRequest);


        StringRequest weightAndHeightRequest = new StringRequest(Request.Method.POST, getWeightHeightURL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray weightAndHeightArray = new JSONArray(response);
                    if(weightAndHeightArray.length()>0){
                        int heightInches = Integer.valueOf(weightAndHeightArray.getJSONObject(0).getString("height"));
                        int feet = heightInches / 12;
                        int inches = heightInches % 12;
                        height.setText(feet + "'" + inches + "''");
                        weight.setText(weightAndHeightArray.getJSONObject(0).getString("weight") + " LBS");
                        age.setText(weightAndHeightArray.getJSONObject(0).getString("age") + "Years");

                        actualWeight = weightAndHeightArray.getJSONObject(0).getString("weight");
                        feetString = String.valueOf(feet);
                        inchesString = String.valueOf(inches);
                        actualAge = weightAndHeightArray.getJSONObject(0).getString("age");
                        isWHGiven = true;
                    }
                    else{
                        height.setText("Not Given");
                        weight.setText("Not Given");
                        age.setText("Not Given");
                        isWHGiven = false;

                        actualWeight ="";
                        feetString = "";
                        inchesString="";
                        actualAge="";
                    }

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
        queue.add(weightAndHeightRequest);
    }


    private void showUpdateScreen(){
        //Set the textviews to invisible
        firstName.setVisibility(View.INVISIBLE);
        lastName.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        height.setVisibility(View.INVISIBLE);
        weight.setVisibility(View.INVISIBLE);
        age.setVisibility(View.INVISIBLE);
        //Set edit button to invisible
        editInfoButton.setVisibility(View.INVISIBLE);

        //Set the editTexts to the textViews values
        firstNameEdit.setText(firstName.getText().toString());
        lastNameEdit.setText(lastName.getText().toString());
        emailEdit.setText(email.getText().toString());
        heightEditFeet.setText(feetString);
        heightEditInches.setText(inchesString);
        weightEdit.setText(actualWeight);
        ageEdit.setText(actualAge);

        //Set the editTexts to visible
        firstNameEdit.setVisibility(View.VISIBLE);
        lastNameEdit.setVisibility(View.VISIBLE);
        emailEdit.setVisibility(View.VISIBLE);
        heightEditFeet.setVisibility(View.VISIBLE);
        heightEditInches.setVisibility(View.VISIBLE);
        weightEdit.setVisibility(View.VISIBLE);
        ageEdit.setVisibility(View.VISIBLE);

        //Set the unit value textViews to visible
        weightEditUnits.setVisibility(View.VISIBLE);
        heightFeetUnits.setVisibility(View.VISIBLE);
        heightInchesUnits.setVisibility(View.VISIBLE);
        ageYearsUnits.setVisibility(View.VISIBLE);
        //Set cancel and submit buttons to visible
        cancelChangesButton.setVisibility(View.VISIBLE);
        submitChangesButton.setVisibility(View.VISIBLE);
    }

    private void showViewingScreen(){
        //Set the textviews to visible
        firstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        height.setVisibility(View.VISIBLE);
        weight.setVisibility(View.VISIBLE);
        age.setVisibility(View.VISIBLE);
        //Set edit button to invisible
        editInfoButton.setVisibility(View.VISIBLE);


        //Set the editTexts to invisible
        firstNameEdit.setVisibility(View.INVISIBLE);
        lastNameEdit.setVisibility(View.INVISIBLE);
        emailEdit.setVisibility(View.INVISIBLE);
        heightEditFeet.setVisibility(View.INVISIBLE);
        heightEditInches.setVisibility(View.INVISIBLE);
        weightEdit.setVisibility(View.INVISIBLE);
        ageEdit.setVisibility(View.INVISIBLE);

        //Set the unit value textViews to invisible
        weightEditUnits.setVisibility(View.INVISIBLE);
        heightFeetUnits.setVisibility(View.INVISIBLE);
        heightInchesUnits.setVisibility(View.INVISIBLE);
        ageYearsUnits.setVisibility(View.INVISIBLE);
        //Set cancel and submit buttons to visible
        cancelChangesButton.setVisibility(View.INVISIBLE);
        submitChangesButton.setVisibility(View.INVISIBLE);
    }


    private void updateInfo(){
        String updateUserInfoURL = "http://proj-309-ab-5.cs.iastate.edu/Client/UpdateUserInfo.php";
        StringRequest updateUserInfoRequest = new StringRequest(Request.Method.POST, updateUserInfoURL, new Response.Listener<String>() {
            public void onResponse(String response) {


                if(isWHGiven) {
                    String updateWeightAndHeightURL = "http://proj-309-ab-5.cs.iastate.edu/Client/UpdateWeightAndHeight.php";
                    StringRequest updateWeightAndHeightRequest = new StringRequest(Request.Method.POST, updateWeightAndHeightURL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            loadUserInfo();
                            showViewingScreen();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userID", userID);
                            int newFeetVal = Integer.valueOf(heightEditFeet.getText().toString());
                            int newInchesVal = Integer.valueOf(heightEditInches.getText().toString());
                            String totalInchesString = String.valueOf((newFeetVal*12) + newInchesVal);
                            params.put("height", totalInchesString);
                            params.put("weight", weightEdit.getText().toString());
                            params.put("age", ageEdit.getText().toString());
                            return params;
                        }
                    };
                    queue.add(updateWeightAndHeightRequest);

                }
                else{
                    String createWeightAndHeightURL = "http://proj-309-ab-5.cs.iastate.edu/Client/CreateWeightAndHeight.php";
                    StringRequest createWeightAndHeightRequest = new StringRequest(Request.Method.POST, createWeightAndHeightURL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            loadUserInfo();
                            showViewingScreen();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userID", userID);
                            int newFeetVal = Integer.valueOf(heightEditFeet.getText().toString());
                            int newInchesVal = Integer.valueOf(heightEditInches.getText().toString());
                            String totalInchesString = String.valueOf((newFeetVal*12) + newInchesVal);
                            params.put("height", totalInchesString);
                            params.put("weight", weightEdit.getText().toString());
                            params.put("age", ageEdit.getText().toString());
                            return params;
                        }
                    };
                    queue.add(createWeightAndHeightRequest);
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
                params.put("firstname", firstNameEdit.getText().toString());
                params.put("lastname", lastNameEdit.getText().toString());
                params.put("email", emailEdit.getText().toString());
                return params;
            }
        };
        queue.add(updateUserInfoRequest);
    }
}
