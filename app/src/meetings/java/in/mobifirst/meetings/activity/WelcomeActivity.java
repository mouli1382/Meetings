package in.mobifirst.meetings.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.SignInButton;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.authentication.FirebaseAuthenticationManager;
import in.mobifirst.meetings.authentication.google.GoogleSignInActivity;
import in.mobifirst.meetings.ftu.SettingsFetcherActivity;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import io.fabric.sdk.android.Fabric;

/**
 * ToDo Show Welcome screen explaining the app functionality in a paginated view.
 * But for now just animating the app name.*
 * Initializes Firebase while showing the splash animation.
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    @Inject
    protected FirebaseAuthenticationManager mFirebaseAuth;

    @Inject
    protected IQSharedPreferences mIQSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_welcome);
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        if (isInternetConnected() == false)
            showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Application needs to connect to Wifi or MobileData")
                .setCancelable(false)
                .setPositiveButton("Connect to Wifi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Connect to MobileData", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadGoogleSignInActivity() {
//        SettingsActivity.start(WelcomeActivity.this);
        GoogleSignInActivity.start(WelcomeActivity.this);
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        bootUp();
    }

    private void bootUp() {

        if (mFirebaseAuth.getAuthInstance().getCurrentUser() != null) {
            Intent intent;
//            if (mIQSharedPreferences.getBoolean(ApplicationConstants.FTU_COMPLETED_KEY)) {
//                intent = new Intent(this, TokensActivity.class);
//            } else {
            intent = new Intent(this, SettingsFetcherActivity.class);
//            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        } else {
            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadGoogleSignInActivity();
                }
            });
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
