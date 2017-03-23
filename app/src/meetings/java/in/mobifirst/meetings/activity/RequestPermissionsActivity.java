package in.mobifirst.meetings.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.tokens.TokensActivity;

import static in.mobifirst.meetings.util.ApplicationConstants.PERMISSION_RECEIVE_SMS;
import static in.mobifirst.meetings.util.ApplicationConstants.REQUEST_CODE_RECEIVE_SMS;

public class RequestPermissionsActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static void start(Context caller) {
        Intent intent = new Intent(caller, RequestPermissionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermission(this, PERMISSION_RECEIVE_SMS)) {
            startOnBoardingActivity();
        } else {
            requestForReceiveSMSPermission();
        }
    }

    private void requestForReceiveSMSPermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUEST_CODE_RECEIVE_SMS, PERMISSION_RECEIVE_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_RECEIVE_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            }

            //Start OnBoardingActivity irrespective of the grants
            startOnBoardingActivity();

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startOnBoardingActivity() {
        startActivity(new Intent(RequestPermissionsActivity.this, TokensActivity.class));
        finish();
    }
}


