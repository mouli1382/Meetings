package in.mobifirst.meetings.storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.meetings.preferences.IQSharedPreferences;

@Module
public class StorageModule {

    @Provides
    @Singleton
    public FirebaseStorageManager provideFirebaseStorageManager(IQSharedPreferences iqSharedPreferences) {
        return new FirebaseStorageManager(iqSharedPreferences);
    }
}