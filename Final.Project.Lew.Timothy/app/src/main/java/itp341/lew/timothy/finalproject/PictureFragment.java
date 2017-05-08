package itp341.lew.timothy.finalproject;


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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import itp341.lew.timothy.finalproject.model.RelevantUserSingleton;
import itp341.lew.timothy.finalproject.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {

    private EditText imageURL;
    private ImageView image;
    private Button preview;
    private Button savePhoto;
    private TextView imagePrompt;
    private TextView header;

    public PictureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        // Get references to UI components.
        imageURL = (EditText) v.findViewById(R.id.imageURL);
        preview = (Button) v.findViewById(R.id.loadImage);
        savePhoto = (Button) v.findViewById(R.id.savePhoto);
        image = (ImageView) v.findViewById(R.id.profile_image);
        imagePrompt = (TextView) v.findViewById(R.id.imagePrompt);
        header = (TextView) v.findViewById(R.id.profilePrompt);

        // Set font.
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        imageURL.setTypeface(typeface);
        preview.setTypeface(typeface);
        savePhoto.setTypeface(typeface);
        imagePrompt.setTypeface(typeface);
        Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
        header.setTypeface(typeface1);

        // Load user profile image for preview (if one exists)
        Glide.with(getActivity().getApplicationContext()).load(RelevantUserSingleton.getInstance(getActivity())
            .getCurrentUser().getPhotoURL().toString().trim()).dontAnimate().placeholder(R.drawable.defaultprofile).into(image);

        // When the preview button is clicked, attempt to load the inputted URL into the preview image frame.
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageURL.getText().toString().trim().equals(""))
                {
                    imageURL.setError("You can't leave this empty!");
                }
                else {
                    Glide.with(getActivity().getApplicationContext()).load(imageURL.getText().toString().trim())
                            .dontAnimate().placeholder(R.drawable.defaultprofile).into(image);
                }
            }
        });

        // When the save photo button is clicked, save whatever's in the imageURL field as the user's profile image.
        savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageURL.getText().toString().trim().equals("")) {
                    imageURL.setError("You can't leave this empty!");
                }
                else {
                    // Update the relevant information in all places that the information exists.
                    User user = RelevantUserSingleton.getInstance(getActivity()).getCurrentUser();
                    user.setPhotoURL(imageURL.getText().toString().trim());
                    RelevantUserSingleton.getInstance(getActivity()).setCurrentUser(user);
                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).setValue(user);

                    // Switch fragments back to the Account fragment.
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment f = (Fragment) new AccountFragment();
                    ft.replace(R.id.homeFrame, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return v;
    }

}
