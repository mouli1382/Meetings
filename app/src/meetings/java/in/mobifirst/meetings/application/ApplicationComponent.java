package in.mobifirst.meetings.application;

import javax.inject.Singleton;

import dagger.Component;
import in.mobifirst.meetings.activity.BaseDrawerActivity;
import in.mobifirst.meetings.activity.WelcomeActivity;
import in.mobifirst.meetings.addedittoken.AddEditTokenFragment;
import in.mobifirst.meetings.authentication.AuthenticationModule;
import in.mobifirst.meetings.authentication.FirebaseAuthenticationManager;
import in.mobifirst.meetings.authentication.google.GoogleSignInActivity;
import in.mobifirst.meetings.config.ResetPreference;
import in.mobifirst.meetings.token.TokensRepository;
import in.mobifirst.meetings.token.TokensRepositoryModule;
import in.mobifirst.meetings.database.DatabaseModule;
import in.mobifirst.meetings.database.FirebaseDatabaseManager;
import in.mobifirst.meetings.ftu.SettingsFetcherActivity;
import in.mobifirst.meetings.ftu.SettingsFragment;
import in.mobifirst.meetings.preferences.IQSharedPreferences;
import in.mobifirst.meetings.sms.SmsReceiver;
import in.mobifirst.meetings.storage.FirebaseStorageManager;
import in.mobifirst.meetings.storage.StorageModule;
import in.mobifirst.meetings.tokens.SnapFragment;
import in.mobifirst.meetings.tokens.TokensFragment;

@Singleton
@Component(modules = {
        TokensRepositoryModule.class,
        ApplicationModule.class,
        AuthenticationModule.class,
        DatabaseModule.class,
        StorageModule.class
})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(GoogleSignInActivity googleSignInActivity);

    void inject(SettingsFetcherActivity settingsFetcherActivity);

    void inject(SettingsFragment settingsFragment);

    void inject(TokensFragment tokensFragment);

    void inject(AddEditTokenFragment addEditTokenFragment);

    void inject(SnapFragment snapFragment);

    void inject(BaseDrawerActivity baseDrawerActivity);

    void inject(SmsReceiver smsReceiver);

    void inject(ResetPreference resetPreference);

    TokensRepository getTokensRepository();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();

    FirebaseDatabaseManager getFirebaseDatabaseManager();

    FirebaseStorageManager getFirebaseStorageManager();

    IQSharedPreferences getIQSharedPreferences();
}
