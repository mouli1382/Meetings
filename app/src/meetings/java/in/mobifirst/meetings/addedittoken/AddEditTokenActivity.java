package in.mobifirst.meetings.addedittoken;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.activity.BaseActivity;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.util.ActivityUtilities;

public class AddEditTokenActivity extends BaseActivity {

    public static final int REQUEST_ADD_TOKEN = 1;
    public static final int REQUEST_EDIT_TOKEN = 2;
    public static final String INTENT_EXTRA_TOKEN_ID = "intent_extra_token_id";

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

        String tokenId = getIntent().getStringExtra(INTENT_EXTRA_TOKEN_ID);
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);
        if (TextUtils.isEmpty(tokenId)) {
            actionBar.setTitle(R.string.issue_token);
            fab.setImageResource(R.drawable.ic_done);
        } else {
            actionBar.setTitle(R.string.edit_token);
            fab.setImageResource(R.drawable.ic_edit);
        }

        if (addEditTokenFragment == null) {
            addEditTokenFragment = AddEditTokenFragment.newInstance();

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
