package in.mobifirst.meetings.database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.meetings.application.IQStoreApplication;
import in.mobifirst.meetings.preferences.IQSharedPreferences;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public FirebaseDatabaseManager provideFirebaseDatabaseManager(IQStoreApplication application, IQSharedPreferences iqSharedPreferences) {
        return new FirebaseDatabaseManager(application, iqSharedPreferences);
    }
}