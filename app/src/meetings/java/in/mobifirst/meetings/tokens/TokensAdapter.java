package in.mobifirst.meetings.tokens;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.util.TimeUtils;


public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;
    private Context mContext;

    public TokensAdapter(Context context, List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        mContext = context;
        setList(items);
        mTokenItemListener = tokenItemListener;
    }

    public TokensAdapter(List<Token> items) {
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
                .inflate(R.layout.item_meeting, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Token token = mTokens.get(position);


        if (token.isActive()) {
            holder.mStatusTextView.setText("ACT");
            holder.mStatusTextView.setTextColor(Color.parseColor("#1B5E20"));
        } else if (token.isCompleted()) {
            holder.mStatusTextView.setText("COM");
            holder.mStatusTextView.setTextColor(Color.parseColor("#607D8B"));
        } else if (token.isCancelled()) {
            holder.mStatusTextView.setText("CAN");
            holder.mStatusTextView.setTextColor(Color.parseColor("#EF5350"));
        } else {
            holder.mStatusTextView.setText("SCH");
            holder.mStatusTextView.setTextColor(Color.parseColor("#673AB7"));
        }


//        if (token.isActive()) {
//            holder.mStatusImageButton.setImageResource(R.drawable.ic_alarm_black_24dp);
//        } else if (token.isCompleted()) {
//            holder.mStatusImageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
//        } else if (token.isCancelled()) {
//            holder.mStatusImageButton.setImageResource(R.drawable.ic_highlight_off_black_24dp);
//        } else {
//            holder.mStatusImageButton.setImageResource(R.drawable.ic_schedule_black_24dp);
//        }

        holder.mTitle.setText(token.getTitle());
        holder.mDescription.setText(token.getDescription());

        holder.mTokenBuzzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call token Buzz
                mTokenItemListener.onActivateTokenClick(token);
            }
        });

        holder.mTokenCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call token Buzz
                mTokenItemListener.onCancelTokenClick(token);
            }
        });

        holder.mTokenCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call tokenComplete
                mTokenItemListener.onCompleteTokenClick(token);
            }
        });

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
        protected ImageButton mTokenBuzzButton;
        protected ImageButton mTokenCancelButton;
        protected ImageButton mTokenCompleteButton;

        //        protected ImageButton mStatusImageButton;
        protected TextView mStatusTextView;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mDescription = (TextView) view.findViewById(R.id.description);
            mStartTime = (TextView) view.findViewById(R.id.startTime);
            mEndTime = (TextView) view.findViewById(R.id.endTime);
            mTokenBuzzButton = (ImageButton) view.findViewById(R.id.imageBuzzButton);
            mTokenCancelButton = (ImageButton) view.findViewById(R.id.imageCancelButton);
            mTokenCompleteButton = (ImageButton) view.findViewById(R.id.imageCompleteButton);

//            mStatusImageButton = (ImageButton) view.findViewById(R.id.statusImageButton);
            mStatusTextView = (TextView) view.findViewById(R.id.statusTextView);
        }
    }
}
