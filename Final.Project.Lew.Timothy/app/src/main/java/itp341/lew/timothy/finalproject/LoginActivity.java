package itp341.lew.timothy.finalproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity implements OnFragmentButtonClickedListener {

    private FragmentManager fm;
    private Fragment f;
    private FragmentTransaction ft;

    // Override on back pressed to account for our fragments in the Login phase.
    public void onBackPressed () {
        fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // On creaton of the Login Activity, create a login fragment and load it in our Activity.
        fm = getSupportFragmentManager();
        f = fm.findFragmentById(R.id.loginFrame);
        ft = fm.beginTransaction();
        if(f == null) {
            f = new LoginFragment();
            ft.add(R.id.loginFrame, f);
        }
        else {
            ft.replace(R.id.loginFrame, f);
        }
        ft.commit();
    }

    // If we need to switch fragments, do it according to the code we get.
    // The code is based on which Fragment we want to switch to.
    public void onFragmentSwitchIndicator(int i) {
        // Switch to the Registration Fragment
        if(i == 0) {
            ft = fm.beginTransaction();
            f = new RegistrationFragment();
            ft.replace(R.id.loginFrame, f);
            ft.addToBackStack(null);
            ft.commit();
        }
        // Switch to the Password Reset Fragment
        else if (i == 1) {
            ft = fm.beginTransaction();
            f = new PasswordResetFragment();
            ft.replace(R.id.loginFrame, f);
            ft.addToBackStack(null);
            ft.commit();
        }
        // Switch to the Login Fragment
        else if (i == 2) {
            ft = fm.beginTransaction();
            f = new LoginFragment();
            ft.replace(R.id.loginFrame, f);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
