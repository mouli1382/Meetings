package in.mobifirst.meetings.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.activity.BaseDrawerActivity;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.display.TokenDisplayService;
import in.mobifirst.meetings.util.ActivityUtilities;

public class TokensActivity extends BaseDrawerActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, TokensActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        SnapFragment snapFragment = (SnapFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);
        if (snapFragment == null) {
            snapFragment = SnapFragment.newInstance();
            actionBar.setTitle(R.string.meetings);
            ActivityUtilities.replaceFragmentToActivity(
                    getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);
        }

        // Create the presenter
        DaggerTokensComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
                .inject(TokensActivity.this);

//        SwitchCompat flow = (SwitchCompat) findViewById(R.id.switchCompat);
//        flow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean issueFlow) {
//                if (compoundButton.getId() == R.id.switchCompat) {
//                    if (!issueFlow) {
//                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
//                        frameLayout.removeAllViewsInLayout();
//                        SnapFragment snapFragment = SnapFragment.newInstance();
//                        ActivityUtilities.replaceFragmentToActivity(
//                                getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);
//
//                        // Create the presenter
//                        DaggerTokensComponent.builder()
//                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
//                                .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
//                                .inject(TokensActivity.this);
//                    } else {
//                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
//                        frameLayout.removeAllViewsInLayout();
//                        TokensFragment tokensFragment = TokensFragment.newInstance();
//                        ActivityUtilities.replaceFragmentToActivity(
//                                getSupportFragmentManager(), tokensFragment, R.id.content_base_drawer);
//
//                        // Create the presenter
//                        DaggerTokensComponent.builder()
//                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
//                                .tokensPresenterModule(new TokensPresenterModule(tokensFragment)).build()
//                                .inject(TokensActivity.this);
//                    }
//                }
//            }
//        });
//        if (!flow.isChecked()) {
//            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
//            frameLayout.removeAllViewsInLayout();
//            SnapFragment snapFragment = SnapFragment.newInstance();
//            ActivityUtilities.replaceFragmentToActivity(
//                    getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);
//
//            // Create the presenter
//            DaggerTokensComponent.builder()
//                    .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
//                    .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
//                    .inject(TokensActivity.this);
//        }

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TokensFilterType currentFiltering =
                    (TokensFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTokensPresenter.setFiltering(currentFiltering);
        }

        startService(new Intent(this, TokenDisplayService.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
//            Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTokensPresenter != null)
            outState.putSerializable(CURRENT_FILTERING_KEY, mTokensPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, TokenDisplayService.class));
        super.onDestroy();
    }
}
