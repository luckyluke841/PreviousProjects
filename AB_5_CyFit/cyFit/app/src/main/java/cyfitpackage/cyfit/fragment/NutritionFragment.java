package cyfitpackage.cyfit.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.LogFoodActivity;
import cyfitpackage.cyfit.other.Food;
import cyfitpackage.cyfit.other.FoodAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;

import static android.app.Activity.RESULT_OK;

public class NutritionFragment extends Fragment {

    private RequestQueue queue;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private WebView pieChart;
    private WebView lineChart;
    private RecyclerView recView;
    private TextView caloriesRemaining;
    private TextView caloriesTotal;
    private TextView currentWeight;
    private List<Food> foods;
    private String userID;
    private double dailyCalories;

    /**
     * Instantiates a new Nutrition fragment.
     */
    public NutritionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NutritionFragment.
     */
    public static NutritionFragment newInstance() {
        NutritionFragment fragment = new NutritionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState stored fragment data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userID = args.getString("userID");
        queue = RequestQueueSingleton.getInstance(getContext()).getRequestQueue();
    }

    /**
     * instantiates buttons for the framelayout creates the layoutmanager and calls getNutrition() to to populate cardViews
     *
     * @param inflater creates view
     * @param container current context
     * @param savedInstanceState stored fragment data
     * @return new view
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_nutrition, container, false);
        final Button dateSelect = (Button) fl.findViewById(R.id.btnFoodDateSelect);
        final Button addNewFood = (Button) fl.findViewById(R.id.btnAddFood);
        final Button weighIn = (Button) fl.findViewById(R.id.btnWeighIn);
        caloriesRemaining = (TextView) fl.findViewById(R.id.caloriesRemaining);
        caloriesTotal = (TextView) fl.findViewById(R.id.caloriesTotal);
        currentWeight = (TextView) fl.findViewById(R.id.CurrentWeight);
        pieChart = (WebView) fl.findViewById(R.id.macroChart);
        lineChart = (WebView) fl.findViewById(R.id.WeightChart);
        recView = (RecyclerView) fl.findViewById(R.id.foodRV);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recView.setLayoutManager(llm);
        foods = new ArrayList<>();
        recView.setAdapter(new FoodAdapter(foods));
        WebSettings pieSettings = pieChart.getSettings();
        pieSettings.setJavaScriptEnabled(true);
        pieChart.requestFocusFromTouch();
        WebSettings lineSettings = lineChart.getSettings();
        lineSettings.setJavaScriptEnabled(true);
        lineChart.requestFocusFromTouch();
        dateSelect.setText(sdf.format(calendar.getTime()));
        dateSelect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            dateSelect.setText(sdf.format(calendar.getTime()));
                            generatePageContent(Long.toString(calendar.getTimeInMillis()));
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

        addNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogFoodActivity.class);
                intent.putExtra("DATE", Long.toString(calendar.getTimeInMillis()));
                intent.putExtra("USER", userID);
                startActivityForResult(intent, 1);
            }
        });

        weighIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(getActivity());
                numberPicker.setMinValue(100);
                numberPicker.setMaxValue(500);
                numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Pounds");
                alertDialogBuilder.setPositiveButton("Log Weight",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                int weight = numberPicker.getValue();
                                logWeight(weight, Long.toString(calendar.getTimeInMillis()));
                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
                alertDialogBuilder.setView(numberPicker);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        generatePageContent(Long.toString(calendar.getTimeInMillis()));

        return fl;
    }

    /**
     *
     * @param context fragment context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                generatePageContent(Long.toString(calendar.getTimeInMillis()));
            }
        }
    }

    private void generatePageContent(final String date) {

        //AJAX call to get food data to generate charts
        final String url1 = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFoodTotal.php";
        final StringRequest genChartRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Chart Response", response);
                try {
                    JSONObject foodTotal = new JSONObject(response);
                    String protein = foodTotal.getString("protein");
                    String carbs = foodTotal.getString("carbs");
                    String fat = foodTotal.getString("fat");
                    Double calories = Double.parseDouble(foodTotal.getString("calories"));
                    Double remaining = dailyCalories - calories;
                    String newTotal = "Total: " + calories.toString();
                    String newRemaining = "Remaining: " + remaining.toString();
                    caloriesTotal.setText(newTotal);
                    caloriesRemaining.setText(newRemaining);
                    pieChart.loadDataWithBaseURL( "file:///android_asset/", generateFoodChart(protein, carbs, fat), "text/html", "utf-8", null );
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
                params.put("date", date);
                params.put("user", userID);
                return params;
            }
        };

        //AJAX call to get list of foods for viewing
        foods.clear();
        final String url2 = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFoods.php";
        final StringRequest genListRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("List Response", response);
                try {
                    JSONArray foodList = new JSONArray(response);
                    int size = foodList.length();
                    if (size != 0) {
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
                    } else {
                        foods.add(new Food("1234", "No Foods Logged Today", "", "Click on 'Log Food' to Get Started"));
                    }
                    recView.setAdapter(new FoodAdapter(foods));
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

        final String url3 = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWeightAndHeight.php";
        final StringRequest getCurrentWeight = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    Log.d("Server", response);
                    JSONArray userInfo = new JSONArray(response);
                    int weight = Integer.parseInt(userInfo.getJSONObject(0).getString("weight"));
                    int height = Integer.parseInt(userInfo.getJSONObject(0).getString("height"));
                    int age = Integer.parseInt(userInfo.getJSONObject(0).getString("age"));
                    String gender = userInfo.getJSONObject(0).getString("gender");
                    //Harris-Benedict Equation
                    if (gender.equals("male")) {
                        dailyCalories = Math.floor((6.2 * weight) + (12.7 * height) - (6.76 * age) + 66.5);
                    } else  {
                        dailyCalories = Math.floor((4.35 * weight) + (4.7 * height) - (4.7 * age) + 655.1);
                    }
                    String curWeight = weight + " lbs";
                    currentWeight.setText(curWeight);
                    queue.add(genChartRequest);
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

        final String url4 = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWeightHistory.php";
        final StringRequest genWeightChart = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    Log.d("Server", response);
                    JSONArray data = new JSONArray(response);
                    lineChart.loadDataWithBaseURL( "file:///android_asset/", generateWeightChart(data), "text/html", "utf-8", null );
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
                params.put("date", date);
                return params;
            }
        };

        queue.add(getCurrentWeight);
        queue.add(genListRequest);
        queue.add(genWeightChart);
    }

    private void logWeight(final int weight, final String date) {
        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/LogWeight.php";
        final StringRequest logWeightRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("Server", response);
                generatePageContent(Long.toString(calendar.getTimeInMillis()));
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
                params.put("weight", Integer.toString(weight));
                params.put("date", date);
                return params;
            }
        };
        queue.add(logWeightRequest);
    }

    private String generateFoodChart(String protein, String carbs, String fat) {
        return "<html>\n" +
                "  <head>\n" +
                "    <!--Load the AJAX API-->\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "\n" +
                "      // Load the Visualization API and the corechart package.\n" +
                "      google.charts.load('current', {'packages':['corechart']});\n" +
                "\n" +
                "      // Set a callback to run when the Google Visualization API is loaded.\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      // Callback that creates and populates a data table,\n" +
                "      // instantiates the pie chart, passes in the data and\n" +
                "      // draws it.\n" +
                "      function drawChart() {\n" +
                "\n" +
                "        // Create the data table.\n" +
                "        var data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Macros');\n" +
                "        data.addColumn('number', 'Amount');\n" +
                "        data.addRows([\n" +
                "          ['Protein', " + Double.parseDouble(protein) + "],\n" +
                "          ['Carbs', " + Double.parseDouble(carbs) + "],\n" +
                "          ['Fat', " + Double.parseDouble(fat) + "],\n" +
                "        ]);\n" +
                "\n" +
                "        // Set chart options\n" +
                "        var options = {'title':'Macronutrients (Grams)',\n" +
                "                       'legend': {position: 'none'},\n" +
                "                       'width':150,\n" +
                "                       'height':175};\n" +
                "\n" +
                "        // Instantiate and draw our chart, passing in some options.\n" +
                "        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "\n" +
                "  <body>\n" +
                "    <!--Div that will hold the pie chart-->\n" +
                "    <div id=\"chart_div\"></div>\n" +
                "  </body>\n" +
                "</html>";
    }

    private String generateWeightChart(JSONArray history) {
        String chart =
                "<html>"+
                "  <head>"+
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"+
                "    <script type=\"text/javascript\">"+
                "      google.charts.load('current', {'packages':['corechart']});"+
                "      google.charts.setOnLoadCallback(drawChart);"+
                ""+
                "      function drawChart() {"+
                "        var data = google.visualization.arrayToDataTable"+
                "            ([['Date', 'Weight', {'type': 'string', 'role': 'style'}],";

        int size = history.length();
        for (int i = 0; i < size; i++) {
            try {
                if (i == (size - 1) ) {
                    chart += "[\'" + history.getJSONObject(i).getString("date") + "\', " + Integer.parseInt(history.getJSONObject(i).getString("weight")) + ", null]";
                    break; }
                chart += "[\'" + history.getJSONObject(i).getString("date") + "\', " + Integer.parseInt(history.getJSONObject(i).getString("weight")) + ", null],";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("Chart", chart);
        chart +=
                "        ]);"+
                ""+
                "        var options = {"+
                "          legend: 'none',"+
                "        'title':'Weigh In History',"+
                "          hAxis: { textPosition: 'none' },"+
                          //vAxis: { minValue: 100, maxValue: 200, title: 'Pounds'},
                "          curveType: 'function',"+
                "          pointSize: 7,"+
                "          dataOpacity: 0.3"+
                "        };"+
                ""+
                "        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));"+
                "        chart.draw(data, options);"+
                "    }"+
                "    </script>"+
                "  </head>"+
                "  <body>"+
                "    <div id=\"chart_div\" style=\"width: 150px; height: 150px;\"></div>"+
                "  </body>"+
                "</html>";
        return  chart;
    }
}
