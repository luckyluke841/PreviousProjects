package cyfitpackage.cyfit.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.AddFriendActivity;
import cyfitpackage.cyfit.activity.FriendsActivity;
import cyfitpackage.cyfit.activity.GoalsActivity;
import cyfitpackage.cyfit.other.RequestQueueSingleton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link FriendCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendCheckFragment extends android.app.DialogFragment {
    String friendEmail;
    String myUserID;
    private Button sendButton, cancelButton;
    private AddFriendActivity activity;
    final RequestQueue queue = RequestQueueSingleton.getInstance(getContext()).getRequestQueue();
    final FriendCheckFragment thisFrag = this;
    private TextView friendFullName, friendEmailText;


    public FriendCheckFragment(){

    }
    public void setMyUserID(String myUserID){
        this.myUserID=myUserID;
    }
    public void setActivity(AddFriendActivity activity){
        this.activity=activity;
    }
    /**
     * Create a new instance of DeleteGoalFragment, providing "userID" and "goalID"
     * as an argument.
     */
    public static FriendCheckFragment newInstance(String friendEmail) {
        FriendCheckFragment f = new FriendCheckFragment();

        Bundle args = new Bundle();
        args.putString("friendEmail", friendEmail);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendEmail = getArguments().getString("friendEmail");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_check, container, false);
        getDialog().setTitle("Check Friend");

        friendEmailText = (TextView) v.findViewById(R.id.potential_friend_email);
        friendFullName = (TextView) v.findViewById(R.id.potential_friend_full_name);
        cancelButton = (Button) v.findViewById(R.id.cancel_friend_button);
        sendButton = (Button) v.findViewById(R.id.send_friend_button_fragment);


        final String getUserInfoURL = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByEmail.php";
        StringRequest getInfoRequest = new StringRequest(Request.Method.POST, getUserInfoURL, new Response.Listener<String>() {
            public void onResponse(String response) {
                try{
                    JSONObject friendInfo = new JSONObject(response);

                    friendEmailText.setText(friendInfo.getString("email"));
                    friendFullName.setText(friendInfo.getString("firstname") + " " + friendInfo.getString("lastname"));


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
                params.put("email", friendEmail);
                return params;
            }
        };

        queue.add(getInfoRequest);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity addFriendActivity = new AddFriendActivity();
                addFriendActivity.sendFriend(activity, myUserID,friendEmail);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(thisFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
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