package in.mobifirst.meetings.addedittoken;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.activity.BaseActivity;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.util.ActivityUtilities;

public class AddEditTokenActivity extends BaseActivity {

    public static final int REQUEST_ADD_TOKEN = 1;

    @Inject
    AddEditTokenPresenter mAddEditTokensPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtoken);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTokenFragment addEditTokenFragment =
                (AddEditTokenFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String tokenId = null;
        if (addEditTokenFragment == null) {
            addEditTokenFragment = AddEditTokenFragment.newInstance();

            actionBar.setTitle(R.string.issue_token);

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTokenFragment, R.id.contentFrame);
        }

        DaggerAddEditTokenComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .addEditTokenPresenterModule(new AddEditTokenPresenterModule(addEditTokenFragment, tokenId))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
