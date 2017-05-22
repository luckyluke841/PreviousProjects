package cyfitpackage.cyfit.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.fragment.DeleteGoalFragment;
import cyfitpackage.cyfit.fragment.FriendCheckFragment;
import cyfitpackage.cyfit.fragment.FriendInfoFragment;
import cyfitpackage.cyfit.other.Friend;
import cyfitpackage.cyfit.other.FriendAdapter;
import cyfitpackage.cyfit.other.GoalAdapter;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.WorkoutGoal;

import static android.R.attr.fragment;

public class FriendsActivity extends AppCompatActivity {

    private List<Friend> myFriends = new ArrayList<Friend>();
    private RecyclerView friendView;
    private RequestQueue queue;
    private String userID;
    private FragmentManager fm;
    private FriendInfoFragment friendInfoFragment;
    Button deleteButton;
    TextView deleteFriendName;





    @Override
    /**
     * Creates Activity for viewing, adding, and removing friends
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getIntent().getStringExtra("USER_ID");



        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deleteButton = (Button) findViewById(R.id.delete_friend_button);
        deleteFriendName = (TextView) findViewById(R.id.delete_friend_name);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.friendsFrameLayout);
        friendView = (RecyclerView) frameLayout.findViewById(R.id.friendsrv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        friendView.setLayoutManager(llm);

        getFriends();

        final FriendsActivity activity =this;
        friendView.setAdapter(new FriendAdapter(myFriends, activity));

        FloatingActionButton fab = (FloatingActionButton) frameLayout.findViewById(R.id.addFriendsfab);
        final Intent addNewFriendIntent = new Intent(this, AddFriendActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFriendIntent.putExtra("USER_ID", userID);
                startActivity(addNewFriendIntent);
                finish();
            }
        });


    }


    private void getFriends(){
        queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetFriends.php";
        final FriendsActivity activity = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONArray friendData = new JSONArray(response);

                    for (int i = 0; i < friendData.length(); i++) {
                        Friend currentFriend = new Friend();
                        currentFriend.setFriendName(friendData.getJSONObject(i).get("friendName").toString());
                        myFriends.add(currentFriend);
                        friendView.setAdapter(new FriendAdapter(myFriends, activity));
                    }
                }catch (JSONException e) {
                    System.out.println("Caught an exception with the response");
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

        queue.add(stringRequest);
    }


    public void displayDelete(final String friendName){
        final FriendsActivity activity = this;
        queue = RequestQueueSingleton.getInstance(activity.getApplicationContext()).getRequestQueue();

        deleteButton.setVisibility(View.VISIBLE);
        deleteFriendName.setText(friendName);
        deleteFriendName.setVisibility(View.VISIBLE);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByName.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                                final String friendsID = jsonArray.getJSONObject(0).getString("ID");
                            String url2 = "http://proj-309-ab-5.cs.iastate.edu/Client/DeleteFriend.php";
                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                                public void onResponse(String response) {
                                    Context context = activity.getApplicationContext();
                                    reloadFriends();
                                    CharSequence text = "Deleted Friend";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
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
                                    params.put("userIDMe", userID);
                                    params.put("userIDFriend", friendsID);
                                    return params;
                                }
                            };

                            queue.add(stringRequest2);
                        }catch(JSONException e){

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
                        params.put("firstname", friendName);
                        return params;
                    }
                };

                queue.add(stringRequest);

            }
        });

    }

    public void reloadFriends(){
        myFriends = new ArrayList<Friend>();
        getFriends();
    }



}
