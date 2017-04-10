package in.mobifirst.meetings.addedittoken;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import javax.inject.Inject;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.fragment.BaseFragment;
import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import in.mobifirst.meetings.receiver.TTLocalBroadcastManager;
import in.mobifirst.meetings.util.NetworkConnectionUtils;
import in.mobifirst.meetings.util.TimeUtils;


public class AddEditTokenFragment extends BaseFragment implements AddEditTokenContract.View {

    private AddEditTokenContract.Presenter mPresenter;

    @Inject
    IQSharedPreferences iqSharedPreferences;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private TextInputLayout mTitleInputLayout;
    private TextInputEditText mTitleEditText;
//    private TextInputLayout mDescriptionInputLayout;
//    private TextInputEditText mDescriptionEditText;

    private Button mDateButton;
    private Button mStartTimeButton;
    private Button mEndTimeButton;

    private int mStartHour;
    private int mEndHour;

    private int mStartMinute;
    private int mEndMinute;

    private long mStartTime;
    private long mEndTime;

    private String mDateString;
    private long mDate;

    public static AddEditTokenFragment newInstance() {
        return new AddEditTokenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);

        mPresenter.subscribe();
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                showNetworkError(getView());
            } else {
                mPresenter.subscribe();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(getView());
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
//        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
//        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull AddEditTokenContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkConnectionUtils.isConnected()) {
                    if (validateInput()) {
                        //ToDo hardcoding to IND country code as of now.
                        mPresenter.addNewMeeting(mTitleEditText.getText().toString()
                                , /*mDescriptionEditText.getText().toString()*/""
                                , mStartTime, mEndTime, mDate);
                    }
                }
            }
        });
    }

    public void showDatePickerDialog(final View v) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setiDatePickerCallback(new DatePickerDialogFragment.IDatePickerCallback() {
            @Override
            public void onDatePicked(int year, int month, int day) {
                final Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                mDate = c.getTimeInMillis();
                mDateString = TimeUtils.getDate(mDate);
                ((Button) v).setText(mDateString);
            }
        });
        datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private IDatePickerCallback iDatePickerCallback;

        interface IDatePickerCallback {
            void onDatePicked(int year, int month, int day);
        }

        public void setiDatePickerCallback(IDatePickerCallback iDatePickerCallback) {
            this.iDatePickerCallback = iDatePickerCallback;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            iDatePickerCallback.onDatePicked(year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private ITimePickerCallback iTimePickerCallback;

        interface ITimePickerCallback {
            void onTimerSet(int hourOfDay, int minute);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void setiTimePickerCallback(ITimePickerCallback iTimePickerCallback) {
            this.iTimePickerCallback = iTimePickerCallback;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            iTimePickerCallback.onTimerSet(hourOfDay, minute);
        }
    }

    public void showTimePickerDialog(final View v, final boolean isStartTime) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setiTimePickerCallback(new TimePickerFragment.ITimePickerCallback() {
            @Override
            public void onTimerSet(int hourOfDay, int minute) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                if (isStartTime) {
                    mStartHour = hourOfDay;
                    mStartMinute = minute;
                    mStartTime = c.getTimeInMillis();
                } else {
                    mEndHour = hourOfDay;
                    mEndMinute = minute;
                    mEndTime = c.getTimeInMillis();
                }
                ((Button) v).setText(hourOfDay + ":" + minute);
            }
        });
        timePickerFragment.show(getActivity().getSupportFragmentManager(), ((Button) v).getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addmeeting, container, false);

        mTitleEditText = (TextInputEditText) root.findViewById(R.id.titleInputEditText);
        mTitleInputLayout = (TextInputLayout) root.findViewById(R.id.titleInputLayout);

//        mDescriptionEditText = (TextInputEditText) root.findViewById(R.id.descriptionInputEditText);
//        mDescriptionInputLayout = (TextInputLayout) root.findViewById(R.id.descriptionInputLayout);

        mDateButton = (Button) root.findViewById(R.id.date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        mStartTimeButton = (Button) root.findViewById(R.id.startTime);
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v, true);
            }
        });

        mEndTimeButton = (Button) root.findViewById(R.id.endTime);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v, false);
            }
        });

        setRetainInstance(true);
        return root;
    }

    private boolean validateInput() {
        CharSequence title = mTitleEditText.getText();
        if (TextUtils.isEmpty(title)) {
            mTitleInputLayout.setError(getString(R.string.invalid_title));
            return false;
        } else {
            mTitleInputLayout.setError("");
        }

//        CharSequence description = mDescriptionEditText.getText();
//        if (TextUtils.isEmpty(description)) {
//            mDescriptionInputLayout.setError(getString(R.string.invalid_description));
//            return false;
//        } else {
//            mDescriptionInputLayout.setError("");
//        }

        if (TextUtils.isEmpty(mDateString)) {
            Snackbar.make(getView(), getString(R.string.invalid_date), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (mStartHour > mEndHour) {
            Snackbar.make(getView(), getString(R.string.invalid_hour), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (mStartHour == mEndHour && (mStartMinute == mEndMinute)) {
            Snackbar.make(getView(), getString(R.string.invalid_minute), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (mStartHour == mEndHour && (mStartMinute > mEndMinute)) {
            Snackbar.make(getView(), getString(R.string.invalid_hour), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void showEmptyTokenError() {
        Snackbar.make(getView(), getString(R.string.empty_token_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTokensList() {
        //todo: Find a better way to avoid crash
        if (getActivity() == null)
            return;
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void populateMeeting(Token token) {
        if (isAdded() && token != null) {
            mTitleEditText.setText(token.getTitle());
//            mDescriptionEditText.setText("");

            mDate = token.getDate();
            mDateString = TimeUtils.getDate(mDate);
            mDateButton.setText(mDateString);

            mStartTime = token.getStartTime();
            mStartTimeButton.setText(TimeUtils.getHourMinute(mStartTime));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mStartTime);
            mStartHour = c.get(Calendar.HOUR_OF_DAY);
            mStartMinute = c.get(Calendar.MINUTE);

            mEndTime = token.getEndTime();
            mEndTimeButton.setText(TimeUtils.getHourMinute(mEndTime));
            c.setTimeInMillis(mEndTime);
            mEndHour = c.get(Calendar.HOUR_OF_DAY);
            mEndMinute = c.get(Calendar.MINUTE);
        }
    }
}
