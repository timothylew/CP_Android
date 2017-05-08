package itp341.lew.timothy.finalproject;


import android.app.Activity;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private OnFragmentButtonClickedListener mCallback;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button login;
    private Button register;
    private Button forgotPassword;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView errorTextView;
    public static final String TAG = "itp341.timothy.lew.tag";

    public LoginFragment() {
        // Required empty public constructor
    }

    // Taken from a firebase tutorial, but method is deprecated.  This makes sure the callback interface
    // is implemented.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFragmentButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnButtonClickedListener");
        }
    }

    // Adds an Auth Listener
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Removes the Auth Listener
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    // Code from Firebase tutorial for how to set up Authentication
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

    // Sign in method, adapted from Firebase tutorial.
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            errorTextView.setText(task.getException().getMessage());
                        }
                        else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()) {
                                // If the user pressed back to get back to login, we want to clear singleton data.
                                RelevantUserSingleton.getInstance(getActivity()).clear();

                                // Create intent to go to main Home Activity of the app.
                                Intent i = new Intent(getActivity(), HomeActivity.class);
                                getActivity().startActivity(i);
                            }
                            else {
                                errorTextView.setText("Your email isn't verified yet, so we just resent you a verification email!");
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
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the view.
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Get references to all UI components.
        login = (Button) v.findViewById(R.id.login);
        register = (Button) v.findViewById(R.id.register);
        forgotPassword = (Button) v.findViewById(R.id.forgotPassword);
        emailInput = (EditText) v.findViewById(R.id.email);
        passwordInput = (EditText) v.findViewById(R.id.password);
        errorTextView = (TextView) v.findViewById(R.id.loginErrorText);

        // Set font for entire fragment.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        login.setTypeface(typeface);
        register.setTypeface(typeface);
        forgotPassword.setTypeface(typeface);
        emailInput.setTypeface(typeface);
        passwordInput.setTypeface(typeface);
        errorTextView.setTypeface(typeface);

        // Attempts to login to account using Firebase methods.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
                if(isValid(emailInput.getText().toString(), passwordInput.getText().toString())) {
                    signIn(emailInput.getText().toString(), passwordInput.getText().toString());
                }
            }
        });

        // Switch fragment to registration fragment.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
                mCallback.onFragmentSwitchIndicator(0);
            }
        });

        // Switch fragment to password reset fragment.
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
                mCallback.onFragmentSwitchIndicator(1);
            }
        });
        return v;
    }

    // Makes sure input fields are not blank.
    private boolean isValid(String email, String password) {
        if(email.trim().equals("")){
            emailInput.setError("Your email cannot be blank.");
            return false;
        }
        else if(password.trim().equals("")) {
            passwordInput.setError("Your password cannot be blank.");
            return false;
        }
        return true;

    }

}
