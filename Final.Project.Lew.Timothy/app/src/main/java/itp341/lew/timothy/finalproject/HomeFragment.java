package itp341.lew.timothy.finalproject;


import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ListView results;
    private TextView userInformation;
    private Button findTutors;
    public HomeActivity.TutorAdapter tutorAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Get references to UI components.
        results = (ListView) v.findViewById(R.id.listView);
        findTutors = (Button) v.findViewById(R.id.findTutorButton);
        TextView matchedTutors = (TextView) v.findViewById(R.id.matchedTutors);

        // Set fonts
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
        matchedTutors.setTypeface(typeface);
        findTutors.setTypeface(typeface);

        // Set up the adapter for this screen.
        ArrayList<User> tempUsers = RelevantUserSingleton.getInstance(getActivity()).getRelevantUserArray();
        final HomeActivity ha = (HomeActivity) getActivity();
        tutorAdapter = ha.new TutorAdapter(getActivity(), tempUsers);
        results.setAdapter(tutorAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the current user's information and store it in Relevant User Singleton for later use.
        FirebaseDatabase.getInstance().getReference().child("users").
                child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                RelevantUserSingleton.getInstance(getActivity()).setCurrentUser(u);
                ha.refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.print(databaseError.getMessage());
            }
        });

        // When we click the find tutors button, switch over to the Account Fragment.
        findTutors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = (Fragment) new AccountFragment();
                ft.replace(R.id.homeFrame, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }

}
