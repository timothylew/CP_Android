package itp341.lew.timothy.finalproject;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private Button changePassword;
    private FirebaseAuth mAuth;
    private Button addTutor;
    private Button requestHelp;
    private Button manageUser;
    private EditText classIDTutor;
    private EditText classIDHelp;
    private TextView accountSettings;
    private TextView prompt1;
    private TextView prompt2;

    public AccountFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        // Get instance of FirebaseAuth and get the references to UI components
        mAuth = FirebaseAuth.getInstance();
        changePassword = (Button) v.findViewById(R.id.accountChangePassword);
        addTutor = (Button) v.findViewById(R.id.addTutor);
        requestHelp = (Button) v.findViewById(R.id.requestHelp);
        classIDTutor = (EditText) v.findViewById(R.id.class_tutor);
        classIDHelp = (EditText) v.findViewById(R.id.class_needhelp);
        manageUser = (Button) v.findViewById(R.id.manageUser);
        accountSettings = (TextView) v.findViewById(R.id.accountSettings);
        prompt1 = (TextView) v.findViewById(R.id.prompt1);
        prompt2 = (TextView) v.findViewById(R.id.prompt2);

        FirebaseUser user = mAuth.getCurrentUser();

        // Set fonts.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        changePassword.setTypeface(typeface);
        addTutor.setTypeface(typeface);
        requestHelp.setTypeface(typeface);
        classIDTutor.setTypeface(typeface);
        classIDHelp.setTypeface(typeface);
        manageUser.setTypeface(typeface);
        prompt2.setTypeface(typeface);
        prompt1.setTypeface(typeface);

        // Set another font.
        Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
        accountSettings.setTypeface(typeface1);

        // When we add ourselves as a tutor, we need to update our Firebase database and reflect the changes.
        // We should also update our singleton's current user.
        addTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validInput(1)){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    myRef.child("classes").child(classIDTutor.getText().toString().trim()).
                            child(user.getUid()).setValue(user);
                    RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().
                            addTutorClass(classIDTutor.getText().toString().trim());
                    myRef.child("users").child(user.getUid()).setValue(RelevantUserSingleton.getInstance(getActivity())
                            .getCurrentUser());
                    Toast.makeText(getActivity(), classIDTutor.getText().toString().trim() + " has been " +
                            "added to your list of tutor topics.", Toast.LENGTH_LONG).show();
                    classIDTutor.setText("");
                }
            }
        });

        // When we request help, we need to update our Firebase database and reflect the changes.
        // We should also update our singleton's current user.
        requestHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validInput(2)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    myRef.child("help").child(user.getUid()).child(classIDHelp.getText().toString().trim()).setValue(user);
                    RelevantUserSingleton.getInstance(getActivity()).getCurrentUser().
                            addHelpClass(classIDHelp.getText().toString().trim());
                    myRef.child("users").child(user.getUid()).setValue(RelevantUserSingleton.getInstance(getActivity())
                        .getCurrentUser());
                    Toast.makeText(getActivity(), classIDHelp.getText().toString().trim() + " has been " +
                            "added to your list of help-needed topics.", Toast.LENGTH_LONG).show();
                    classIDHelp.setText("");
                }
            }
        });

        // When the change password button is clicked, send the user a password reset email.
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "A password reset link has been sent to the email associated with this account.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // When the manage user button is clicked, switch to the add profile picture fragment.
        manageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = (Fragment) new PictureFragment();
                ft.replace(R.id.homeFrame, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }

    // Function to ensure edit text fields are not blank when adding classes.
    public boolean validInput(int i) {
        boolean valid = true;

        // Check to make sure the fields taking in the appropriate input are valid.
        if(i == 1)
        {
            if(classIDTutor.getText().toString().trim().equals("")) {
                valid = false;
                classIDTutor.setError("Please make sure this field is not empty.");
            }
        }
        else if (i == 2) {
            if(classIDHelp.getText().toString().trim().equals("")) {
                valid = false;
                classIDHelp.setError("Please make sure this field is not empty.");
            }
        }
        return valid;
    }

}
