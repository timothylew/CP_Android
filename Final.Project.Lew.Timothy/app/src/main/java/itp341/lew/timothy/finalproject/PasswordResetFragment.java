package itp341.lew.timothy.finalproject;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordResetFragment extends Fragment {

    private Button sendPasswordReset;
    private EditText emailToSendTo;
    private Button backToLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView errorTextView;
    public static final String TAG = "itp341.timothy.lew.tag";

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    // On start get instance of Firebase Auth
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // On stop remove the Firebase Auth Listener
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    // Code adapted from the Firebase tutorial code.
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
        View v = inflater.inflate(R.layout.fragment_password_reset, container, false);

        // Get references to UI components.
        sendPasswordReset = (Button) v.findViewById(R.id.sendRecoveryEmail);
        emailToSendTo = (EditText) v.findViewById(R.id.emailForgotPassword);
        errorTextView = (TextView) v.findViewById(R.id.loginErrorText);
        backToLogin = (Button) v.findViewById(R.id.passwordToLogin);

        // Set font for UI components.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        sendPasswordReset.setTypeface(typeface);
        emailToSendTo.setTypeface(typeface);
        errorTextView.setTypeface(typeface);
        backToLogin.setTypeface(typeface);

        // Send user a password reset email to the specified email address when the reset button is clicked.
        sendPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
            String emailAddress = emailToSendTo.getText().toString();
            if(!emailAddress.trim().equals("")) {
                mAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                // Error checks to make sure email field is not empty.
                emailToSendTo.setError("Email address cannot be empty.");
                errorTextView.setText("Email address cannot be empty.");
            }
            }
        });

        // When back to login button is pressed, go back to login fragment and pop off backstack.
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return v;
    }
}
