package cyfitpackage.cyfit.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.fragment.DeleteGoalFragment;
import cyfitpackage.cyfit.fragment.FriendCheckFragment;

/**
 * Created by victordasilva on 4/9/17.
 */

public class AddFriendActivity extends AppCompatActivity {

    private String userID;
    EditText friendEmail;
    FragmentManager fm;
    FriendCheckFragment friendCheckFragment;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        userID = getIntent().getStringExtra("USER_ID");



        final Button sendFriendRequestButton = (Button) findViewById(R.id.SendFriendRequestButton);

        friendEmail = (EditText) findViewById(R.id.FriendEmail);
        final AddFriendActivity activity = this;
        sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fm = getFragmentManager();
                friendCheckFragment = FriendCheckFragment.newInstance(friendEmail.getText().toString());
                friendCheckFragment.setMyUserID(userID);
                friendCheckFragment.setActivity(activity);
                friendCheckFragment.show(fm, "Friend Check");

            }
        });
    }

    public void sendFriend(Activity activity, String userIDString, String friendEmailString){
        final String userID = userIDString;
        final String friendEmail = friendEmailString;
        final RequestQueue queue = Volley.newRequestQueue(activity);
        final Activity passActivity = activity;
        final Context context = activity.getApplicationContext();
        final String getUserInfoURL = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByEmail.php";
        StringRequest getFriendInfo = new StringRequest(Request.Method.POST, getUserInfoURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject friendInfo = new JSONObject(response);


                    final String friendUserID = friendInfo.getString("ID");
                    final String friendName = friendInfo.getString("firstname");


                    final String getMyName = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByID.php";

                    StringRequest getMyNameRequest = new StringRequest(Request.Method.POST, getMyName, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject meObject = new JSONObject(response);

                                final String myName = meObject.getString("firstname");

                                final String createFriendShipURL = "http://proj-309-ab-5.cs.iastate.edu/Client/CreateFriendship.php";

                                StringRequest addFriendRequest = new StringRequest(Request.Method.POST, createFriendShipURL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //goBackToFriendsActivity(userID);
                                        CharSequence text = "Added New Friend!";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
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

                                        params.put("userNameFriend", friendName);
                                        params.put("userIDFriend", friendUserID);
                                        params.put("userIDMe", userID);
                                        params.put("userNameMe", myName);
                                        return params;
                                    }
                                };
                                queue.add(addFriendRequest);


                            }catch(JSONException e){

                            }
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
                            return params;
                        }
                    };
                    queue.add(getMyNameRequest);

                }catch(JSONException e){

                }
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
                params.put("email", friendEmail);
                return params;
            }
        };
        queue.add(getFriendInfo);
    }


    public void goBackToFriendsActivity(String userID){
        Intent backToFriends = new Intent(this, cyfitpackage.cyfit.activity.FriendsActivity.class);
        backToFriends.putExtra("USER_ID", userID);
        startActivity(backToFriends);
        finish();
    }
}






