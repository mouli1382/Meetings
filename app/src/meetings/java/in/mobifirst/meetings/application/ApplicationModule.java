package in.mobifirst.meetings.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import in.mobifirst.meetings.util.NetworkConnectionUtils;

@Module
public class ApplicationModule {

    private IQStoreApplication application;

    public ApplicationModule(IQStoreApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public IQStoreApplication provideIQApplication() {
        return application;
    }

    @Provides
    @Singleton
    public IQSharedPreferences provideIQSharedPreferences() {
        return new IQSharedPreferences(application);
    }

    @Provides
    @Singleton
    NetworkConnectionUtils provideNetworkConnectionUtils() {
        return new NetworkConnectionUtils(application);
    }
}