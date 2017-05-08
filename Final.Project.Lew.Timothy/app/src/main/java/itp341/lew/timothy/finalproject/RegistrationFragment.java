package itp341.lew.timothy.finalproject;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button backToLogin;
    private EditText confirmPassword;
    private Button registration;
    private FirebaseAuth mAuth;
    private TextView errorTextView;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String TAG = "itp341.timothy.lew.tag";

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    // Code adapted from Firebase tutorial
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else {
                    // user signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registration, container, false);

        // Get references to UI components.
        name = (EditText) v.findViewById(R.id.nameInput);
        email = (EditText) v.findViewById(R.id.emailInput);
        password = (EditText) v.findViewById(R.id.passwordInput);
        confirmPassword = (EditText) v.findViewById(R.id.confirmPasswordInput);
        registration = (Button) v.findViewById(R.id.registerInput);
        errorTextView = (TextView) v.findViewById(R.id.loginErrorText);
        backToLogin = (Button) v.findViewById(R.id.registerToLogin);

        // Set font for all UI Components.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        name.setTypeface(typeface);
        email.setTypeface(typeface);
        password.setTypeface(typeface);
        confirmPassword.setTypeface(typeface);
        registration.setTypeface(typeface);
        errorTextView.setTypeface(typeface);
        backToLogin.setTypeface(typeface);

        // When register button is clicked, make sure input is valid.  Then, use createAccount firebase method.
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
                errorTextView.setTextColor(Color.parseColor("#FF0000"));
                if(isValid()) {
                    createAccount(email.getText().toString(), password.getText().toString());
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    // Create account method adapted from Firebase tutorial.
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // if sign in fails, display message.
                        // if sign in succeeds, state listener is notified and logic to handle signed in user is handled in listener
                        if(!task.isSuccessful()) {
                            errorTextView.setText(task.getException().getMessage());
                        }
                        else {
                            sendEmail();

                            errorTextView.setTextColor(Color.parseColor("#228B22"));
                            errorTextView.setText("Awesome, check your email to verify your account!");

                            // Set up this new user in our Firebase database.
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            User newUser = new User(name.getText().toString().trim(), user.getEmail(), user.getUid(), 0, 0);
                            myRef.child("users").child(user.getUid()).setValue(newUser);
                        }
                    }
                });

    }

    // Code to send user a verification email once an account is created.
    private void sendEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    // Error checking for input in the registration form.
    private boolean isValid() {
        if(name.getText().toString().trim().equals("")) {
            name.setError("Name cannot be empty.");
            return false;
        }
        else if(email.getText().toString().trim().equals("")) {
            email.setError("Email cannot be empty.");
            return false;
        }
        else if(password.getText().toString().trim().equals("")) {
            password.setError("Password cannot be empty.");
            return false;
        }
        else if(confirmPassword.getText().toString().trim().equals("")) {
            confirmPassword.setError("This field cannot be empty.");
            return false;
        }
        else if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
            errorTextView.setText("Passwords do not match.");
            return false;
        }
        return true;
    }


}
