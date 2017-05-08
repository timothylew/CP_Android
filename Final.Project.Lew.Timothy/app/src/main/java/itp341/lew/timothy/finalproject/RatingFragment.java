package itp341.lew.timothy.finalproject;


import android.graphics.Typeface;
import android.os.Bundle;

import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompatBase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends Fragment {

    public static final String RATING_POS = "timothylew.itp341.finalproject.ratingposition";
    public static final String INPUT_POS = "timothylew.itp341.finalproject.inputposition";
    private int position;
    private int inputType;
    private User toRate;

    public RatingFragment() {
        // Required empty public constructor
    }

    // newInstance method to pass information into Fragment during creation.
    public static RatingFragment newInstance(int x, int y) {
        Bundle args = new Bundle();
        args.putInt(RATING_POS, x);
        args.putInt(INPUT_POS, y);
        Fragment f = new RatingFragment();
        f.setArguments(args);
        return (RatingFragment) f;
    }

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Get the passed in information from Bundle.
        position = getArguments().getInt(RATING_POS);
        inputType = getArguments().getInt(INPUT_POS);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rating, container, false);

        // Get references to UI components.
        TextView nameView = (TextView) v.findViewById(R.id.ratingUser);
        Button submitRating = (Button) v.findViewById(R.id.submitRating);
        TextView ratingPrompt = (TextView) v.findViewById(R.id.ratingPrompt);
        final RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        ImageView image = (ImageView) v.findViewById(R.id.profile_rating);

        // Set fonts for UI components.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        submitRating.setTypeface(typeface);
        ratingPrompt.setTypeface(typeface);
        Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBold.ttf");
        nameView.setTypeface(typeface1);

        // Set name text for user to rate depending on which fragment sent us here.
        // Also set the right profile picture depending on which fragment sent us here.
        if(inputType == 1) {
            nameView.setText(RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray().get(position).getName());
            Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                    .getRelevantUserArray().get(position).getPhotoURL().toString().trim()).dontAnimate().
                    placeholder(R.drawable.defaultprofile).into(image);

        }
        else if (inputType == 2) {
            nameView.setText(RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers().get(position).getName());
            Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
                    .getMessagingUsers().get(position).getPhotoURL().toString().trim()).dontAnimate().
                    placeholder(R.drawable.defaultprofile).into(image);
        }

        // Update ratings and go back to home fragment when the rating is submitted.
        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the appropriate user to rate depending on which fragment sent us here.
                if(inputType == 1) {
                    toRate = RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray().get(position);
                }
                else if (inputType == 2) {
                    toRate = RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers().get(position);
                }

                // Increase number of ratings by 1, and total score by the score given.
                toRate.setNumberOfRatings(toRate.getNumberOfRatings() + 1);
                toRate.setTotalScore(toRate.getTotalScore() + ratingBar.getRating());

                // Update the value of this User in our Firebase database.
                FirebaseDatabase.getInstance().getReference().child("users").child(toRate.getUid()).setValue(toRate);

                // Switch fragments back to the home fragment when the user is done.
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = new HomeFragment();
                ft.replace(R.id.homeFrame, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }

}
