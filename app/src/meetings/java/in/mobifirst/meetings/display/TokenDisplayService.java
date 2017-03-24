package in.mobifirst.meetings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.commonsware.cwac.preso.PresentationService;

import java.util.ArrayList;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.receiver.TTLocalBroadcastManager;
import in.mobifirst.meetings.tokens.MeetingsAdapter;

public class TokenDisplayService extends PresentationService implements
        Runnable {
    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
    private Handler handler = null;
    private RecyclerView mRecyclerView;
    private MeetingsAdapter mMeetingsAdapter;
    private boolean flipMe = false;
    private LinearLayoutManager mLinearLayoutManager;
    //    private GridLayoutManager mGridLayoutManager;
    private View mRootView;

    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Token> tokens = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
            if (tokens != null) {
                mMeetingsAdapter.replaceData(tokens);
            }
        }
    };

    @Override
    public void onCreate() {
        handler = new Handler(Looper.getMainLooper());
        mMeetingsAdapter = new MeetingsAdapter(this);
        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
        super.onCreate();
    }

    @Override
    protected int getThemeId() {
        return (R.style.AppTheme);
    }

    @Override
    protected View buildPresoView(Context context, LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.extended_display, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.display_recyclerview);
//        mGridLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
//        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
//        mRecyclerView.addItemDecoration(itemDecoration);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mMeetingsAdapter);

        run();

        return (mRootView);
    }

    @Override
    public void run() {
        int itemCount = mMeetingsAdapter.getItemCount();
        if (itemCount > 0) {
            int firstVisiblePosition =
                    mLinearLayoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition =
                    mLinearLayoutManager.findLastVisibleItemPosition();
            int window = lastVisiblePosition - firstVisiblePosition;
            int scrollBy = lastVisiblePosition + window / 2;
            Log.e("TokenDisplayService", "scrollBy = " + scrollBy);
            if (scrollBy > 0 && scrollBy < itemCount) {
                mRecyclerView.scrollToPosition(scrollBy);
            } else {
                if (!flipMe) {
                    //Scroll to end to show the non-window multiples
                    mRecyclerView.scrollToPosition(itemCount - 1);
                    flipMe = !flipMe;
                } else {
                    //Scroll to start
                    mRecyclerView.scrollToPosition(0);
                    flipMe = !flipMe;
                }
            }
        }
//        if (flipMe) {
//            int lastVisiblePosition =
//                    mGridLayoutManager.findLastCompletelyVisibleItemPosition();
//            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemCount) {
//                mRecyclerView.scrollToPosition(lastVisiblePosition);
//            }
//            if (flipMe) {
//                //Scroll to end
////                mRecyclerView.scrollToPosition(itemCount - 1);
//
//                } else {
//                    flipMe = !flipMe;
//                }
//            } else {
//                //Scroll to start
//                mRecyclerView.scrollToPosition(0);
//                flipMe = !flipMe;
//            }
//        }
        handler.postDelayed(this, 5000);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);

        super.onDestroy();
    }
}