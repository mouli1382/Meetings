package in.mobifirst.meetings.tokens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.util.TimeUtils;


public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;
    private Context mContext;

    public MeetingsAdapter(Context context, List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        mContext = context;
        setList(items);
        mTokenItemListener = tokenItemListener;
    }

    public MeetingsAdapter(Context context) {
        mContext = context;
        mTokens = new ArrayList<>();
    }

    public MeetingsAdapter(List<Token> items) {
        setList(items);
    }

    public void replaceData(List<Token> tokens) {
        setList(tokens);
        notifyDataSetChanged();
    }

    private void setList(List<Token> tokens) {
        mTokens = tokens;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting_display, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Token token = mTokens.get(position);

        if (token.isActive()) {
            holder.mStatusTextView.setText("ACTIVE");
            holder.mStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        } else if (token.isCompleted()) {
            holder.mStatusTextView.setText("COMPLETED");
            holder.mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        } else if (token.isCancelled()) {
            holder.mStatusTextView.setText("CANCELLED");
            holder.mStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.mStatusTextView.setText("SCHEDULED");
            holder.mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));
        }

        holder.mTitle.setText(token.getTitle());
        holder.mDescription.setText(token.getDescription());

        holder.mStartTime.setText(TimeUtils.getHourMinute(token.getStartTime()));
        holder.mEndTime.setText(TimeUtils.getHourMinute(token.getEndTime()));
    }

    @Override
    public int getItemCount() {
        if (mTokens == null)
            return 0;
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTitle;
        protected TextView mDescription;
        protected TextView mStartTime;
        protected TextView mEndTime;

        protected TextView mStatusTextView;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mDescription = (TextView) view.findViewById(R.id.description);
            mStartTime = (TextView) view.findViewById(R.id.startTime);
            mEndTime = (TextView) view.findViewById(R.id.endTime);

            mStatusTextView = (TextView) view.findViewById(R.id.statusTextView);
        }
    }
}
