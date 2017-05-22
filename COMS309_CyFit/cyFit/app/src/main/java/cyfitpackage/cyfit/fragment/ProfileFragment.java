package cyfitpackage.cyfit.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cyfitpackage.cyfit.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Button myInfoButton, goalsButton, friendsButton, favoriteWorkoutsButton, helpButton;

    private OnFragmentInteractionListener mListener;

    private String userID;
    private String name;

    /**
     * Instantiates a new Profile fragment.
     */
    public ProfileFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
// TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userID = args.getString("userID");
        name = args.getString("name");
    }

    @Override
    /**
     * Instantiate the values of the buttons for the linear layout.
     * Opens up the different Activites corresponding to which button is clicked
     * Returns the linear layout
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        LinearLayout lLayout = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        ((TextView)lLayout.findViewById(R.id.UserName)).setText(name);


        myInfoButton = (Button) lLayout.findViewById(R.id.myInfoButton);
        goalsButton = (Button) lLayout.findViewById(R.id.GoalsButton);
        friendsButton = (Button) lLayout.findViewById(R.id.FriendsButton);
        favoriteWorkoutsButton = (Button) lLayout.findViewById(R.id.FavoriteWorkoutsButton);
        helpButton = (Button) lLayout.findViewById(R.id.HelpButton);


        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cyfitpackage.cyfit.activity.MyInfo.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cyfitpackage.cyfit.activity.GoalsActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cyfitpackage.cyfit.activity.FriendsActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        favoriteWorkoutsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), cyfitpackage.cyfit.activity.FavoriteWorkoutsActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), cyfitpackage.cyfit.activity.HelpingActivity.class));
            }
        });

        return lLayout;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        /**
         * On fragment interaction.
         *
         * @param uri the uri
         */
// TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}
