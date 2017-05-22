package cyfitpackage.cyfit.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.fragment.CalenderFragment;
import cyfitpackage.cyfit.fragment.MapsFragment;
import cyfitpackage.cyfit.fragment.MusicFragment;
import cyfitpackage.cyfit.fragment.NutritionFragment;
import cyfitpackage.cyfit.fragment.ProfileFragment;
import cyfitpackage.cyfit.fragment.WorkoutFragment;
import cyfitpackage.cyfit.other.RequestQueueSingleton;
import cyfitpackage.cyfit.other.SlidingRelativeLayout;
import cyfitpackage.cyfit.other.VolleyMethods;
import cyfitpackage.cyfit.other.Workout;
import cyfitpackage.cyfit.other.WorkoutAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


/**
 * The Home activity.
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private String userID;
    private String fullname;

    private RequestQueue queue;

    private List<Workout> workouts;
    private RecyclerView recView;


    private TextView title_name;
    private TextView title_email;


//    public MediaPlayer MP;
    private Player MP;

    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;

    private static final String CLIENT_ID = "8c1068e25c07438794f30002265f9429";

    private static final String REDIRECT_URI = "my-first-android-app-login://callback";

    private static final int REQUEST_CODE = 1337;

    private SpotifyApi api;

    public SpotifyService spotify;

    private static String ACCESS_TOKEN;

    private ListView playlistList;

    private ArrayAdapter<String> listAdapter;

    /**
     * Instantiates the navigation view and drawer, as well as the bottom bar
     * @param savedInstanceState previous application instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getIntent().getStringExtra("USER_ID");
        queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View navHeaderView = navigationView.getHeaderView(0);
        title_name = (TextView) navHeaderView.findViewById(R.id.title_name);
        title_email = (TextView) navHeaderView.findViewById(R.id.title_email);
        setUserView();

        VolleyMethods.getInstance(this);//asdf


        SlidingRelativeLayout bottomBar = (SlidingRelativeLayout) findViewById(R.id.bottom_Bar);
        bottomBar.setVisibility(View.VISIBLE);
        //bottomBar.setYFraction(10);//TODO figure out why tf this doesn't work
        bottomBar.setY(1555);//TODO change this so that is goes by fraction instead of pixels since not all screens are the same size

        //sets up the horizontal recycle view for a list of cardview workouts visible when the bottom bar is pulled up
        recView = (RecyclerView) bottomBar.findViewById(R.id.bbRV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recView.setLayoutManager(llm);
        workouts = new ArrayList<>();
        recView.setAdapter(new WorkoutAdapter(workouts));
        bbPopulateWorkouts();


        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{
                        "user-read-private",
                        "playlist-read",
                        "playlist-read-private",
                        "streaming" })
                .build();

//        Intent intent = AuthenticationClient.createLoginActivityIntent(this, request);
//        startActivityForResult(intent, REQUEST_CODE);

        AuthenticationClient.openLoginActivity(this, this.REQUEST_CODE, request);

        api = new SpotifyApi();
        spotify = api.getService();

        Button songNameButton = (Button) findViewById(R.id.bbSongNameButton);
        songNameButton.setOnClickListener(this);
        ImageButton bbPlay = (ImageButton) findViewById(R.id.bbPlay);
        bbPlay.setOnClickListener(this);
        ImageButton bbFastForward = (ImageButton) findViewById(R.id.bbFF);
        bbFastForward.setOnClickListener(this);
        ImageButton bbRewind = (ImageButton) findViewById(R.id.bbREW);
        bbRewind.setOnClickListener(this);


//        MP = MediaPlayer.create(this,R.raw.all_star);
        seekbar = (SeekBar)findViewById(R.id.bbSeekBar);
        seekbar.setClickable(false);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bbSongNameButton:
                //toggleHeight();
                tempToggleHeight();
                break;
            case R.id.bbPlay:
                if (MP == null) {
                    Toast.makeText(getApplicationContext(), "The media player is null.", Toast.LENGTH_SHORT).show();
                    break;
                }
                Button songNameButton = (Button) findViewById(R.id.bbSongNameButton);
//                songNameButton.setText("All Star");
//                if(!MP.isPlaying()) {
//                    MP.start();
                if(!MP.getPlaybackState().isPlaying) {
//                    MP.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
                    MP.resume(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
//                    finalTime = MP.getDuration();
                    if (MP.getMetadata().currentTrack == null) break;
                    finalTime = MP.getMetadata().currentTrack.durationMs;
//                    startTime = MP.getCurrentPosition();
                    startTime = MP.getPlaybackState().positionMs;
                    if (oneTimeOnly == 0) {
                        seekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }
                } else {
//                    MP.pause();
                    MP.pause(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
                break;
            case R.id.bbFF:
                int temp = (int)startTime;

                if((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
//                    MP.seekTo((int) startTime);
                    MP.seekToPosition(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    }, (int) startTime);
                } else {
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bbREW:
                int temp2 = (int)startTime;

                if((temp2 - backwardTime) > 0){
                    startTime = startTime - backwardTime;
//                    MP.seekTo((int) startTime);
                    MP.seekToPosition(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    }, (int) startTime);
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                api.setAccessToken(response.getAccessToken());
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        Log.d("INITIALIZED", "PLAYER INITIALIZED");
                        MP = spotifyPlayer;
                        MP.addConnectionStateCallback(HomeActivity.this);
                        MP.addNotificationCallback(HomeActivity.this);
                        seekbar = (SeekBar)findViewById(R.id.bbSeekBar);
                        seekbar.setClickable(false);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
//            startTime = MP.getCurrentPosition();
            startTime = MP.getPlaybackState().positionMs;
            /*tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );*/
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

        /**
         * closes the drawer and returns to the previous screen
         */
        @Override
        public void onBackPressed () {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        /**
         * @param menu menu context for items to be added
         * @return true upon completion
         */
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }




        /**
         * Opens fragment based on NavigationDrawer selction
         *
         * @param item menu item selected
         * @return true upon completion
         */
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            Fragment fragment = null;
            Bundle args = new Bundle();
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else if (id == R.id.nav_workout) {
                fragment = new WorkoutFragment();
            } else if (id == R.id.nav_nutrition) {
                fragment = new NutritionFragment();
            } else if (id == R.id.nav_music) {
                fragment = new MusicFragment();
            } else if (id == R.id.nav_calender) {
                fragment = new CalenderFragment();
            } else if(id == R.id.nav_maps) {
                fragment = new MapsFragment() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }

                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                };
            }
            else if (id == R.id.toggle_bottom_bar) {
                toggleVisibility();
                return true;
            }else if (id == R.id.nav_logout) {
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;
            }

            args.putString("userID", userID);
            args.putString("name", fullname);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_home, fragment).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        /**
         * control the position of the bottom bar using custom anims slide_up.xml and slide_down.xml
         */
        //TODO change toggleHeight() to control the position of the bottom bar using custom anims slide_up.xml and slide_down.xml

    private void toggleHeight() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);

        MusicFragment mf = MusicFragment.newInstance().newInstance();
        //transaction.replace(R.id.fragment_container, mf );    //TODO fix
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void toggleVisibility(){
        SlidingRelativeLayout bottomBar = (SlidingRelativeLayout) findViewById(R.id.bottom_Bar);
        if(bottomBar.getY() >= 3000)
            bottomBar.setY(1555);
        else
            bottomBar.setY(3000);
    }

    private void tempToggleHeight() {
        SlidingRelativeLayout bottomBar = (SlidingRelativeLayout) findViewById(R.id.bottom_Bar);
        if (bottomBar.getY() >= 1000)
            bottomBar.setY(200);
        else
            bottomBar.setY(1555);
    }


    private void bbPopulateWorkouts() {

        workouts = new ArrayList<>();
        RequestQueue queue = RequestQueueSingleton.getInstance(this).getRequestQueue();

        String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetWorkoutsForBrowsing.php";//TODO create this PHP file GetCurrentWorkoutsPlaylist.php

        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("Server", response);
                try {
                    JSONArray workoutData = new JSONArray(response);
                    for (int i = 0; i < workoutData.length(); i++) {
                        workouts.add(new Workout());
                        try {
                            workouts.get(i).setName(workoutData.getJSONObject(i).get("workoutName").toString());
                            workouts.get(i).setDesc(workoutData.getJSONObject(i).get("description").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    recView.setAdapter(new WorkoutAdapter(workouts));
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
                params.put("userID", userID);
                return params;
            }
        };
        queue.add(jsonArrayRequest);
    }

    private void setUserView() {

        final String url = "http://proj-309-ab-5.cs.iastate.edu/Client/GetUserByID.php";

        StringRequest getUserRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("Server", response);
                try {
                    JSONObject user = new JSONObject(response);

                    fullname = user.getString("firstname") + " " + user.getString("lastname");
                    String email = user.getString("email");
                    title_name.setText(fullname);
                    title_email.setText(email);

                    //load user profile when server responds with data
                    Fragment profile = new ProfileFragment();//TODO
                    Bundle args = new Bundle();
                    args.putString("userID", userID);
                    args.putString("name", fullname);

                    profile.setArguments(args);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_home, profile).commit();

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
                params.put("userID", userID);
                return params;
            }
        };
        queue.add(getUserRequest);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Button songNameButton = (Button) findViewById(R.id.bbSongNameButton);
        ImageButton playButton = (ImageButton) findViewById(R.id.bbPlay);


        switch (playerEvent) {
            case kSpPlaybackNotifyPlay:

                if (MP.getMetadata() != null) songNameButton.setText(MP.getMetadata().currentTrack.name + " - " + MP.getMetadata().currentTrack.artistName);
                break;
            case kSpPlaybackNotifyPause:
                break;
            case kSpPlaybackNotifyTrackChanged:

                songNameButton.setText(MP.getMetadata().currentTrack.name + " - " + MP.getMetadata().currentTrack.artistName);
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {

    }

    @Override
    public void onLoggedIn() {
        Log.d("LOGIN", "LOGGED IN");
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    public Player getPlayer() {
        return this.MP;
    }

    public String getAccessToken() {
        return this.ACCESS_TOKEN;
    }
}
