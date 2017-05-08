package itp341.lew.timothy.finalproject;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {


    public MessageFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        // Get references to UI components.
        ListView messageOptions = (ListView) v.findViewById(R.id.messageOptions);
        TextView yourMessages = (TextView) v.findViewById(R.id.yourMessages);

        // Set font for UI components.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
        yourMessages.setTypeface(typeface);

        // Set up the adapter for listview, and create an arraylist for that adapter.
        final ArrayList<User> toAdd = new ArrayList<>();
        final MessageListAdapter mAdapter = new MessageListAdapter(getActivity(), toAdd);
        messageOptions.setAdapter(mAdapter);

        // Set a firebase listener to get relevant data (people you have open message channels with).
        // This will let us know which chats exist with you already.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("chatMap").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // For each chat channel, add the other party into user arraylist in singleton
                        for(DataSnapshot individualItem : dataSnapshot.getChildren()){
                            String key = individualItem.getKey();
                            FirebaseDatabase.getInstance().getReference().child("users")
                                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    toAdd.add(user);
                                    if(!RelevantUserSingleton.getInstance(getActivity()).messagingUserExists(user)) {
                                        RelevantUserSingleton.getInstance(getActivity()).addMessagingUser(user);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return v;
    }


    // This adapter gets all the users with open messaging channels with you, and puts it in the list view.
    public class MessageListAdapter extends ArrayAdapter<User> {

        private ArrayList<User> openChannels;

        // Constructor takes in an ArrayList to use for the tutors.
        public MessageListAdapter(Context context, ArrayList<User> openChannels) {
            super(context, 0, openChannels);
            this.openChannels = openChannels;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_display_item, null);
            }

            // Get the current user for the specific row we're on.
            User user = openChannels.get(position);

            // Get references to UI components.
            TextView name = (TextView) convertView.findViewById(R.id.userNameForMessaging);
            TextView topicsAvailable = (TextView) convertView.findViewById(R.id.topicsAvailable);
            Button respond = (Button) convertView.findViewById(R.id.continueMessenger);
            ImageView image = (ImageView) convertView.findViewById(R.id.profile_messenger);

            // Set the fonts for the UI components.
            Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBold.ttf");
            name.setTypeface(typeface1);
            Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
            topicsAvailable.setTypeface(typeface2);
            respond.setTypeface(typeface2);

            // Load user profile pictures.
            Glide.with(getActivity().getApplicationContext()).load(user.getPhotoURL().toString().trim()).dontAnimate()
                    .placeholder(R.drawable.defaultprofile).into(image);


            // Set the text for the UI Components accordingly to the position we're at.
            name.setText(user.getName());

            // Set the biography text of a user to contain all the topics a user tutors.
            String bioText = "Topics: ";
            ArrayList<String> classes = user.getTutoringClasses();
            for (int i = 0; i < classes.size(); i++) {
                if(i != 0) {
                    if(classes.get(i-1) != null) {
                        bioText += ", ";
                    }
                }
                if(classes.get(i) != null) {
                    bioText += classes.get(i);
                }
            }
            if(bioText.equals("Topics: ")) {
                bioText = "Topics: None";
            }
            topicsAvailable.setText(bioText);

            // Declare int tagPosition that will be our tag for the respond button.
            int tagPosition = -1;

            // Go through arraylist of messaging users to find the position TODO
            ArrayList<User> tempArrayList = RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers();
            for(int i = 0; i < tempArrayList.size(); i++) {
                if(user.getUid().trim().equals(tempArrayList.get(i).getUid().trim())) {
                    tagPosition = i;
                }
            }
            // The position should be the index the user is at in the Relevant user array

            respond.setTag(tagPosition);

            // Set on click listener for the learnMore button.
            respond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();

                    // Use the newInstance method to pass information to Message Room Fragment and switch fragments.
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment f = MessageRoomFragment.newInstance(clickedPosition, 2);
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            return convertView;
        }
    }

}
