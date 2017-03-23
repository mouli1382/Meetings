package in.mobifirst.meetings.ftu;


import android.net.Uri;

import in.mobifirst.meetings.model.Store;
import in.mobifirst.meetings.mvp.BasePresenter;
import in.mobifirst.meetings.mvp.BaseView;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {

        void showUploadFailedError();

        void onFileUploadFinished(Uri uri);

        void showEmptyStoreError();

        void showAddStoreFailedError();

        void showTokensList(Store store);

        boolean isActive();

        void populateStore(Store store);
    }

    interface Presenter extends BasePresenter {
        void uploadFile(byte[] bitmapData);

        void addStoreDetails(Store store);

        void getStoreDetails();
    }
}
