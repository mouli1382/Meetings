package in.mobifirst.meetings.ftu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.activity.BaseActivity;
import in.mobifirst.meetings.activity.RequestPermissionsActivity;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.authentication.FirebaseAuthenticationManager;
import in.mobifirst.meetings.database.FirebaseDatabaseManager;
import in.mobifirst.meetings.model.Store;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import in.mobifirst.meetings.receiver.TTLocalBroadcastManager;
import in.mobifirst.meetings.util.ApplicationConstants;
import in.mobifirst.meetings.util.NetworkConnectionUtils;
import rx.Subscriber;

public class SettingsFetcherActivity extends BaseActivity {

    @Inject
    FirebaseAuthenticationManager mAuthenticationManager;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private ProgressBar mProgressBar;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, SettingsFetcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_fetcher);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        ((IQStoreApplication) getApplication()).getApplicationComponent()
                .inject(this);

        Store.clearStore(mIQSharedPreferences);

        if (mNetworkConnectionUtils.isConnected()) {
            fetchStore();
        } else {
            showNetworkError(mProgressBar);
        }
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);
            if (!isConnected) {
                showNetworkError(mProgressBar);
            } else {
                fetchStore();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(mProgressBar);
        }
        TTLocalBroadcastManager.registerReceiver(SettingsFetcherActivity.this, mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(SettingsFetcherActivity.this, mNetworkBroadcastReceiver);
    }

    private void fetchStore() {
        mFirebaseDatabaseManager.getStoreById(mAuthenticationManager
                        .getAuthInstance().getCurrentUser().getUid(),
                new Subscriber<Store>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);

//                            Snackbar.make(mProgressBar, R.string.failed_fetch_store,
//                                    Snackbar.LENGTH_INDEFINITE)
//                                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                        }
//                                    }).show();
                        }
                    }

                    @Override
                    public void onNext(Store result) {
                        persistStore(result);
                    }
                });
    }

    private void persistStore(Store store) {
        if (store == null) {
            SettingsActivity.start(SettingsFetcherActivity.this);
        } else {
            store.persistStore(mIQSharedPreferences);
            mIQSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
            mIQSharedPreferences.putString(ApplicationConstants.STORE_UID, mAuthenticationManager.getAuthInstance().getCurrentUser().getUid());
            RequestPermissionsActivity.start(SettingsFetcherActivity.this);
        }
        finish();
    }
}
