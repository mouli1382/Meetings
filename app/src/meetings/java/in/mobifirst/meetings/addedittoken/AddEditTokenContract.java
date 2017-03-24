package in.mobifirst.meetings.addedittoken;


import in.mobifirst.meetings.mvp.BasePresenter;
import in.mobifirst.meetings.mvp.BaseView;

public interface AddEditTokenContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTokenError();

        void showTokensList();

//        void setTitle(String title);

//        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addNewMeeting(String title, String description, long startTime, long endTime);

//        void populateToken();
    }
}
