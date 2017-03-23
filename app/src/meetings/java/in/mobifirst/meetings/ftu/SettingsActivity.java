package in.mobifirst.meetings.ftu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.activity.BaseActivity;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import in.mobifirst.meetings.util.ActivityUtilities;
import in.mobifirst.meetings.util.ApplicationConstants;

public class SettingsActivity extends BaseActivity {

    @Inject
    SettingsPresenter mSettingsPresenter;

    IQSharedPreferences mIQSharedPreferences;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        //ToDo inject it - avoid cyclic dependency.
        mIQSharedPreferences = ((IQStoreApplication) getApplicationContext()).getApplicationComponent().getIQSharedPreferences();
        if (mIQSharedPreferences.getBoolean(ApplicationConstants.FTU_COMPLETED_KEY)) {
            actionBar.setTitle(R.string.my_account);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        } else {
            actionBar.hide();
        }

        SettingsFragment settingsFragment =
                (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    settingsFragment, R.id.contentFrame);
        }

        DaggerSettingsComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .settingsPresenterModule(new SettingsPresenterModule(settingsFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
