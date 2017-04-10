package in.mobifirst.meetings.tokens;

import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.mvp.BasePresenter;
import in.mobifirst.meetings.mvp.BaseView;

public interface TokensContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTokens(List<Token> tokens);

        void showSnaps(List<Snap> snaps);

        void showAddToken();

        void showTokenDetailsUi(String tokenId);

        void showTokenMarkedComplete();

        void showTokenMarkedActive();

        void showTokenMarkedCancel();

        void showCompletedTokensCleared();

        void showLoadingTokensError();

        void showNoTokens();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showCancelledFilterLabel();

        void showNoActiveTokens();

        void showNoCompletedTokens();

        void showNoCancelledTokens();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();

        void showCantEditTokenMessage();

        void showDeletedTokenMessage(boolean b);

        void showDeleteConfirmationDialog(Token token, int position);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTokens(boolean forceUpdate);

        void loadTokensMap(boolean forceUpdate);

        void addNewToken();

        void openTokenDetails(@NonNull Token requestedToken);

        void activateToken(@NonNull Token activeToken);

        void completeToken(@NonNull Token completedToken);

        void cancelToken(@NonNull Token activeToken);

        void clearCompletedTokens();

        void setFiltering(TokensFilterType requestType);

        TokensFilterType getFiltering();

        void setCounter(int counter);

        int getCounter();

        void deleteToken(@NonNull Token token, int position, boolean deleteMe);
    }
}
