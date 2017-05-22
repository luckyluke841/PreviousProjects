package cyfitpackage.cyfit.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.w3c.dom.Text;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.HomeActivity;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The music fragment
 */
public class MusicFragment extends Fragment {

    private String userID;

    private ArrayAdapter<String> listAdapter;

//    FrameLayout fl;
    private RelativeLayout layout;

    private ListView playlistList;

    /**
     * Instantiates a new Music fragment.
     */
    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * music fragment.
     *
     * @return A new instance of fragment MusicFragment.
     */
    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads previous application instance if present.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userID = args.getString("userID");

        ((HomeActivity)getActivity()).spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                for(PlaylistSimple p : playlistSimplePager.items) {
                    listAdapter.add(p.name);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     *Sets the music fragment view for the application.
     *
     * @param inflater view inflater
     * @param container current view context
     * @param savedInstanceState previous session data
     * @return music fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_music, container, false);
        playlistList = (ListView) layout.findViewById(R.id.playlistList);
        listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simplerow);
        playlistList.setAdapter(listAdapter);
        playlistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ((HomeActivity)getActivity()).spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
                    @Override
                    public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                        ((HomeActivity)getActivity()).getPlayer().pause(new Player.OperationCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
                        for (PlaylistSimple p : playlistSimplePager.items) {
                            if (p.name.equals(listAdapter.getItem(position))) {
//                                mPlayer.playUri(null, p.uri, 0, 0);
                                ((HomeActivity)getActivity()).getPlayer().playUri(null, p.uri, 0, 0);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
        return layout;
    }

    /**
     * Sets current context for fragment.
     *
     * @param context current application context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Default actions for fragment detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
