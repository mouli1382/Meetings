package in.mobifirst.meetings.config;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.authentication.FirebaseAuthenticationManager;
import in.mobifirst.meetings.database.FirebaseDatabaseManager;

public class ResetPreference extends DialogPreference implements DialogInterface.OnClickListener {

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected FirebaseAuthenticationManager mFirebaseAuth;

    public ResetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogTitle(R.string.reset_title);
        setDialogMessage(R.string.reset);
        setPositiveButtonText(R.string.reset_button);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {

        ((IQStoreApplication) getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);

        return super.onCreateDialogView();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //Reset the token counter in the store.
            mFirebaseDatabaseManager.resetTokenCounter(mFirebaseAuth.getAuthInstance().getCurrentUser().getUid());
        }
    }
}
