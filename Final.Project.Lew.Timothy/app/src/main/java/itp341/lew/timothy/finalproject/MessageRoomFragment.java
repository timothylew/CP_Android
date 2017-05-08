package itp341.lew.timothy.finalproject;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import itp341.lew.timothy.finalproject.model.Chat;
import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageRoomFragment extends Fragment {

    public static final String POSITION_ARG = "timothylew.itp341.cpapp.finalproject.position_arg";
    public static final String INPUT_MODE = "timothylew.itp341.cpapp.finalproject.input_mode";
    private int position;
    private EditText messageInput;
    private Button sendMessage;
    private ListView messages;
    private TextView talkingTo;
    private Button rateTutor;
    private String talkingToUID;
    private FirebaseListAdapter mAdapter;
    private String roomName;
    private int inputMode;
    private String roomName1;
    private String roomName2;
    private Chat chat;
    private ImageView profileImage;
    private TextView topics;
    private TextView rating;

    // New instance method for passing information into this Fragment.
    public static MessageRoomFragment newInstance(int x, int y) {
        Bundle args = new Bundle();
        args.putInt(POSITION_ARG, x);
        args.putInt(INPUT_MODE, y);
        Fragment f = new MessageRoomFragment();
        f.setArguments(args);
        return (MessageRoomFragment) f;
    }

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Retrieve information from Bundle.
        position = getArguments().getInt(POSITION_ARG);
        inputMode = getArguments().getInt(INPUT_MODE);
    }


    public MessageRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message_room, container, false);

        // Get references to UI Components
        messageInput = (EditText) v.findViewById(R.id.inputMessage);
        sendMessage = (Button) v.findViewById(R.id.sendMessage);
        messages = (ListView) v.findViewById(R.id.messages);
        talkingTo = (TextView) v.findViewById(R.id.talkingTo);
        rateTutor = (Button) v.findViewById(R.id.rateYourTutor);
        profileImage = (ImageView) v.findViewById(R.id.profile_messaging);
        topics = (TextView) v.findViewById(R.id.topicsAvailableMessaging);
        rating = (TextView) v.findViewById(R.id.messageRatingDisplay);

        // Set fonts for UI Components
        Typeface typefaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBold.ttf");
        Typeface lightTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        messageInput.setTypeface(lightTypeface);
        talkingTo.setTypeface(typefaceBold);
        sendMessage.setTypeface(lightTypeface);
        rateTutor.setTypeface(lightTypeface);
        topics.setTypeface(lightTypeface);
        rating.setTypeface(typefaceBold);

        // Make the message list view always auto scroll to the bottom (most recent messages).
        messages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messages.setStackFromBottom(true);

        // Depending on which fragment brought us here, generate the appropriate possible room names for chat rooms.
        // Also load the appropriate profile image.
        if(inputMode == 1) {
            roomName1 = RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray().
                    get(position).getUid() + "_" + RelevantUserSingleton.getInstance(getActivity()).getCurrentUser()
                    .getUid();
            roomName2 = RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getUid()
                    + "_" + RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray().get(position)
                    .getUid();
            Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                    .getRelevantUserArray().get(position).getPhotoURL().toString().trim()).dontAnimate().placeholder(R.drawable.defaultprofile)
                    .into(profileImage);

        }
        else if(inputMode == 2) {
            roomName1 = RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers().
                    get(position).getUid() + "_" + RelevantUserSingleton.getInstance(getActivity()).getCurrentUser()
                    .getUid();
            roomName2 = RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getUid()
                    + "_" + RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers().get(position)
                    .getUid();
            Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                    .getMessagingUsers().get(position).getPhotoURL().toString().trim()).dontAnimate().placeholder(R.drawable.defaultprofile)
                    .into(profileImage);
        }


        // Check which room name is the existing/valid one.  If it doesn't exist yet, just create the chat room.
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference().child("channels");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(roomName1)) {
                    roomName = roomName1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(getActivity(), Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }
                else if (dataSnapshot.hasChild(roomName2)) {
                    roomName = roomName2;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(getActivity(), Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }
                else {
                    roomName = roomName1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(getActivity(), Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }

                if(inputMode == 1) {
                    firebaseDatabase.getReference().child("chatMap").child(RelevantUserSingleton.getInstance(getActivity())
                            .getRelevantUserArray().get(position).getUid()).child(RelevantUserSingleton.getInstance(getActivity())
                            .getCurrentUser().getUid()).setValue(roomName);
                    firebaseDatabase.getReference().child("chatMap").child(RelevantUserSingleton.getInstance(getActivity())
                            .getCurrentUser().getUid()).child(RelevantUserSingleton.getInstance(getActivity())
                            .getRelevantUserArray().get(position).getUid()).setValue(roomName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Initialize more information and UI components based on which fragment took us here.
        if(inputMode == 1) {
            // Get some user information
            User tempUser = RelevantUserSingleton.getInstance(getActivity())
                    .getRelevantUserArray().get(position);
            talkingTo.setText(tempUser.getName());
            talkingToUID = tempUser.getUid();

            // If the number of ratings is non-zero, calculate the rating and display it.
            if(tempUser.getNumberOfRatings() > 0) {
                double ratingScore = tempUser.getTotalScore() / tempUser.getNumberOfRatings();
                String printScore = String.format("%.2f", ratingScore);
                rating.setText("Rating: " + printScore + " / 5");

                // Set color of rating text depending on the user's rating.
                if(ratingScore >= 4.0) {
                    rating.setTextColor(Color.parseColor("#35c15e"));
                }
                else if(ratingScore >= 3.0) {
                    rating.setTextColor(Color.parseColor("#b3b24b"));
                }
                else {
                    rating.setTextColor(Color.parseColor("#d65856"));
                }

            }
            else {
                rating.setText("No rating.");
            }

            // Set user biography text to show all the topics a tutor can tutor.
            String bioText = "Topics: ";
            ArrayList<String> classes = tempUser.getTutoringClasses();
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
            topics.setText(bioText);
        }
        else if(inputMode == 2) {
            // Get some user information
            User tempUser = RelevantUserSingleton.getInstance(getActivity())
                    .getMessagingUsers().get(position);
            talkingTo.setText(tempUser.getName());
            talkingToUID = tempUser.getUid();

            // If number of users is non-zero, calculate the rating and display it for a user.
            if(tempUser.getNumberOfRatings() > 0) {
                double ratingScore = tempUser.getTotalScore() / tempUser.getNumberOfRatings();
                String printScore = String.format("%.2f", ratingScore);
                rating.setText("Rating: " + printScore + " / 5");

                // Set color of rating text depending on the user's rating.
                if(ratingScore >= 4.0) {
                    rating.setTextColor(Color.parseColor("#35c15e"));
                }
                else if(ratingScore >= 3.0) {
                    rating.setTextColor(Color.parseColor("#b3b24b"));
                }
                else {
                    rating.setTextColor(Color.parseColor("#d65856"));
                }
            }
            else {
                rating.setText("No rating.");
            }

            // For a user's bio, display a list of all topics the tutor can teach.
            String bioText = "Topics: ";
            ArrayList<String> classes = tempUser.getTutoringClasses();
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
            topics.setText(bioText);
        }

        // When the rate tutor button is clicked, switch over to the rating fragment.
        rateTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change fragments to rating fragment.
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                // Pass in the position and the inputMode paramater, specifying which fragment is sending us to the next
                Fragment f = RatingFragment.newInstance(position, inputMode);
                ft.replace(R.id.homeFrame, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        // This sendMessage listener sends a Chat object over to the Firebase Database.
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure that the roomname is valid, and the message input is valid.
                if(roomName.trim().equals("")) {
                    Toast.makeText(getActivity(), "Please send that message again.", Toast.LENGTH_SHORT).show();
                }
                else if(messageInput.getText().toString().trim().equals("")) {
                    messageInput.setError("This field can't be empty!");
                }
                else {
                    // Construct the Chat object by creating the necessary components.
                    String message = messageInput.getText().toString().trim();
                    final String df = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    if(inputMode == 1) {
                        chat = new Chat(message, df, RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray()
                                .get(position).getName(), RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getName()
                                , talkingToUID, RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getUid());
                    }
                    else if (inputMode == 2) {
                        chat = new Chat(message, df, RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers()
                                .get(position).getName(), RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getName()
                                , talkingToUID, RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().getUid());
                    }
                    FirebaseDatabase.getInstance().getReference().child("channels").child(roomName).child(df).setValue(chat);
                    messageInput.setText("");
                }

            }
        });

        return v;
    }

    public void onDetach() {
        super.onDetach();
        mAdapter.cleanup();
    }

    // Firebase Adapter for all the chat objects under a certain child.
    private class ChatAdapter extends FirebaseListAdapter<Chat> {
        public ChatAdapter(Activity activity, Class<Chat> modelClass, int modelLayout, DatabaseReference ref) {
            super(activity, modelClass, modelLayout, ref);
        }
        protected void populateView(View v, Chat model, int positionNumber) {

            // Get references to UI components.
            TextView message = (TextView) v.findViewById(R.id.messageToDisplay);
            TextView sender = (TextView) v.findViewById(R.id.sentBy);
            TextView timestamp = (TextView) v.findViewById(R.id.messageTimestamp);
            ImageView messageIdentity = (ImageView) v.findViewById(R.id.profile_messageIdentity);

            // Set fonts.
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBold.ttf");
            message.setTypeface(typeface);
            Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
            sender.setTypeface(typeface1);
            timestamp.setTypeface(typeface1);

            // Set text according to the Chat Message object.
            message.setText(model.getMessageContents());
            sender.setText(model.getSenderName());
            timestamp.setText(model.getTimestamp());

            // Display profile pictures based on who is sending a message.
            if(model.getSenderName().equals(RelevantUserSingleton.getInstance(getActivity())
                    .getCurrentUser().getName())) {
                Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                        .getCurrentUser().getPhotoURL().toString().trim()).dontAnimate()
                        .placeholder(R.drawable.defaultprofile)
                        .into(messageIdentity);
            }
            else if(inputMode == 1) {
                Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                        .getRelevantUserArray().get(position).getPhotoURL().toString().trim()).dontAnimate()
                        .placeholder(R.drawable.defaultprofile)
                        .into(messageIdentity);
            }
            else if(inputMode == 2) {
                Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                        .getMessagingUsers().get(position).getPhotoURL().toString().trim()).dontAnimate()
                        .placeholder(R.drawable.defaultprofile)
                        .into(messageIdentity);
            }
        }
    }

}
