package itp341.lew.timothy.finalproject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;

/**
 * Created by timothylew on 4/4/17.
 */

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;
    private ArrayList<String> relevantUserIDs;
    private Toolbar toolbar;
    private FragmentManager fm;
    private Fragment f;
    private FragmentTransaction ft;
    private boolean notificationsEnabled;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Notifications enabled boolean is initialized to false.
        notificationsEnabled = false;

        // Get reference to bottom menu bar
        bottomMenu = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize internal array list for relevant user IDs
        relevantUserIDs = new ArrayList<>();

        // Load the first fragment, which is a normal Home Fragment.
        fm = getSupportFragmentManager();
        f = fm.findFragmentById(R.id.homeFrame);
        ft = fm.beginTransaction();
        if(f == null) {
            f = new HomeFragment();
            ft.add(R.id.homeFrame, f);
        }
        else {
            ft.replace(R.id.homeFrame, f);
        }
        ft.commit();

        // Set the listener for all navigation items on the bottom menu bar.
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // If the home bottom menu bar item is clicked, switch to the home fragment.
                if(item.getItemId() == R.id.bottom_home) {
                    refresh();
                    ft = fm.beginTransaction();
                    f = new HomeFragment();
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }

                // If the message menu bar item is clicked, switch to the message fragment.
                else if(item.getItemId() == R.id.bottom_messenger) {
                    ft = fm.beginTransaction();
                    f = new MessageFragment();
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }

                // If the account menu bar item is clicked, switch to the account fragment.
                else if(item.getItemId() == R.id.bottom_account) {
                    ft = fm.beginTransaction();
                    f = new AccountFragment();
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                return true;
            }
        });

        // A notification is sent to both parties when a new tutor relationship is created.
        FirebaseDatabase.getInstance().getReference().child("chatMap")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(notificationsEnabled) {
                            // Notification code adapted from Firebase tutorial.
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.applogo_cp)
                                            .setContentTitle("New chat channel.")
                                            .setContentText("A new tutor relationship has been initiated!");

                            // Sets an ID for the notification
                            int mNotificationId = 001;

                            // Gets an instance of the NotificationManager service
                            NotificationManager mNotifyMgr =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            // Builds the notification and issues it.
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                        notificationsEnabled = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /* refresh()
       - returns nothing and takes in no parameters
       - this function refreshes the items inside the relevant user singleton so that the home fragment displays
         up to date tutor information */
    public void refresh() {

        //Grab the current user and get the classesNeeded array from that user to see what the user needs help with
        User currentUser =
                RelevantUserSingleton.getInstance(getApplicationContext()).getCurrentUser();
        ArrayList<String> classesNeeded = currentUser.getNeedHelp();

        // Iterate through the classes that are needed to grab a list of available tutors for each class.
        for(int i = 0; i < classesNeeded.size(); i++) {
            // Grab the current className
            final String className = classesNeeded.get(i);

            // Grab data from the Firebase Database containing the user information of tutors for the class.
            FirebaseDatabase.getInstance().getReference().child("classes").child(className).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot individualItem : dataSnapshot.getChildren()){
                        String userID = individualItem.getKey();
                        if(!relevantUserIDs.contains(userID)) {
                            relevantUserIDs.add(userID);
                        }
                    }
                    // For each relevant user, grab their User class and store it in our RelevantUserSingleton
                    for(int i = 0; i < relevantUserIDs.size(); i++) {
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(relevantUserIDs.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User userToAdd = dataSnapshot.getValue(User.class);
                                if(!RelevantUserSingleton.getInstance(getApplicationContext()).relevantUserExists(userToAdd)) {
                                    RelevantUserSingleton.getInstance(getApplicationContext()).addRelevantUser(userToAdd);
                                    // notify the adapter that the data set has changed.
                                    if(f instanceof HomeFragment) {
                                        ((HomeFragment) f).tutorAdapter.notifyDataSetChanged();
                                    }
                                }
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
        }
    }


    // Custom adapter to display all of the potential tutors for the user in the Home Fragment.
    public class TutorAdapter extends ArrayAdapter<User> {

        private ArrayList<User> tutors;

        // Constructor takes in an ArrayList to use for the tutors.
        public TutorAdapter(Context context, ArrayList<User> tutors) {
            super(context, 0, tutors);
            this.tutors = tutors;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_display_item, null);
            }

            // Get the current user for the specific row we're on.
            User user = tutors.get(position);

            // Get references to UI components.
            TextView username = (TextView) convertView.findViewById(R.id.userName);
            TextView biography = (TextView) convertView.findViewById(R.id.biography);
            Button learnMore = (Button) convertView.findViewById(R.id.cardLearnMore);
            TextView ratingText = (TextView) convertView.findViewById(R.id.ratingText);
            TextView ratingScore = (TextView) convertView.findViewById(R.id.ratingScore);
            ImageView image = (ImageView) convertView.findViewById(R.id.profile_card);

            // Set fonts.
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
            username.setTypeface(typeface);
            ratingScore.setTypeface(typeface);
            Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
            biography.setTypeface(typeface1);
            learnMore.setTypeface(typeface1);
            ratingText.setTypeface(typeface1);

            // Set the text for the UI Components accordingly to the position we're at.
            username.setText(user.getName());

            // Load user profile images.
            Glide.with(getApplicationContext()).load(RelevantUserSingleton.getInstance(getApplicationContext())
                    .getRelevantUserArray().get(position).getPhotoURL().toString().trim()).dontAnimate()
                    .placeholder(R.drawable.defaultprofile).into(image);

            // If the number of ratings is non zero, calculate the rating and set the text of our rating Text View.
            if(user.getNumberOfRatings() > 0) {
                double score = user.getTotalScore()/user.getNumberOfRatings();
                String printScore = String.format("%.2f", score);
                ratingScore.setText(printScore + " / 5");

                // Set color of rating text depending on the user's rating.
                if(score >= 4.0) {
                    ratingScore.setTextColor(Color.parseColor("#35c15e"));
                }
                else if(score >= 3.0) {
                    ratingScore.setTextColor(Color.parseColor("#b3b24b"));
                }
                else {
                    ratingScore.setTextColor(Color.parseColor("#d65856"));
                }

            }
            else {
                ratingScore.setText("None.");
            }

            // For the user's bio text field, create a string with all the topics the user tutors.
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
            biography.setText(bioText);

            // Set the tag.
            learnMore.setTag(position);

            // Set on click listener for the learnMore button.
            learnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();

                    // Use the newInstance method to pass information to Message Room Fragment and switch fragments.
                    ft = fm.beginTransaction();
                    f = MessageRoomFragment.newInstance(clickedPosition, 1);
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            return convertView;
        }
    }
}
