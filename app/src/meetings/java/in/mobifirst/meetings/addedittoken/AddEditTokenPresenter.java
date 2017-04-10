package in.mobifirst.meetings.addedittoken;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.token.TokensRepository;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddEditTokenPresenter implements AddEditTokenContract.Presenter {

    @NonNull
    private final TokensRepository mTokensRepository;

    @NonNull
    private final AddEditTokenContract.View mAddTokenView;

    @Nullable
    private String mTokenId;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    AddEditTokenPresenter(@Nullable String TokenId, TokensRepository tokensRepository,
                          AddEditTokenContract.View addTokenView) {
        mTokenId = TokenId;
        mTokensRepository = tokensRepository;
        mAddTokenView = addTokenView;
        mSubscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        mAddTokenView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (mTokenId != null) {
            fetchMeetingDetails();
        }
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void addNewMeeting(String title, String description, long startTime, long endTime, long date) {
        Token token = new Token();
        token.setTitle(title);
        token.setDescription(description);
        token.setStartTime(startTime);
        token.setEndTime(endTime);
        token.setDate(date);
        if (TextUtils.isEmpty(mTokenId)) {
            token.setStatus(Token.Status.ISSUED.ordinal());
            token.setBuzzCount(0);
        } else {
            token.setuId(mTokenId);
        }
        saveToken(token);
    }

    private void saveToken(@NonNull Token token) {
//        if (token.isEmpty()) {
//            mAddTokenView.showEmptyTokenError();
//        } else {
        mTokensRepository.addNewToken(token, new Subscriber<String>() {
            @Override
            public void onCompleted() {
                mAddTokenView.showTokensList();
            }

            @Override
            public void onError(Throwable e) {
                if (mAddTokenView.isActive()) {
                    mAddTokenView.showEmptyTokenError();
                }
            }

            @Override
            public void onNext(String result) {
            }
        });

//        }
    }

    @Override
    public void fetchMeetingDetails() {
        if (mTokenId == null) {
            return;
        }
        Subscription subscription = mTokensRepository
                .getToken(mTokenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Token>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAddTokenView.isActive()) {
                            mAddTokenView.showEmptyTokenError();
                        }
                    }

                    @Override
                    public void onNext(Token token) {
                        if (mAddTokenView.isActive()) {
                            mAddTokenView.populateMeeting(token);
                        }
                    }
                });

        mSubscriptions.add(subscription);
    }
}
