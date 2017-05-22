package cyfitpackage.cyfit.other;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import cyfitpackage.cyfit.R;
import cyfitpackage.cyfit.activity.FriendsActivity;

/**
 * Created by victordasilva on 4/1/17.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<Friend> friendList;
    private static FriendsActivity activity;


    public FriendAdapter(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public FriendAdapter(List<Friend> friendList, FriendsActivity activity) {
        this.friendList = friendList;
        this.activity=activity;
    }



    public int getItemCount() {
        return friendList.size();
    }


    public void onBindViewHolder(FriendViewHolder contactViewHolder, int i) {
        Friend friend = friendList.get(i);
        contactViewHolder.friendName.setText(friend.getFriendName());
    }


    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.friend_card, viewGroup, false);
        return new FriendViewHolder(itemView);
    }


    static class FriendViewHolder extends RecyclerView.ViewHolder {

        /**
         * The friend's name
         */
        TextView friendName;

        /**
         * Instantiates a new ViewHolder for a friend object
         *
         * @param v the specified view
         */
        FriendViewHolder(View v) {
            super(v);
            friendName=(TextView) v.findViewById(R.id.friend_name);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked Friend");
                    activity.displayDelete(friendName.getText().toString());

                }
            });
        }
    }
}
