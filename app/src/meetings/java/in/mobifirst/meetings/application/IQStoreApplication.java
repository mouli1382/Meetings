package in.mobifirst.meetings.application;

import in.mobifirst.meetings.application.DaggerApplicationComponent;
import in.mobifirst.meetings.authentication.AuthenticationModule;
import in.mobifirst.meetings.token.TokensRepositoryModule;
import in.mobifirst.meetings.database.DatabaseModule;
import in.mobifirst.meetings.storage.StorageModule;

public class IQStoreApplication extends IQApplication {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();
    }

    private void setupObjectGraph() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent
                    .builder()
                    .databaseModule(new DatabaseModule())
                    .authenticationModule(new AuthenticationModule())
                    .applicationModule(new ApplicationModule(this))
                    .tokensRepositoryModule(new TokensRepositoryModule())
                    .storageModule(new StorageModule())
                    .build();
        }
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
