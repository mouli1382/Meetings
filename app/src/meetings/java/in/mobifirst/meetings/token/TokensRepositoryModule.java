package in.mobifirst.meetings.token;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.meetings.authentication.FirebaseAuthenticationManager;
import in.mobifirst.meetings.database.FirebaseDatabaseManager;

@Module
public class TokensRepositoryModule {

    private TokensDataSourceImpl mTokensDataSourceImpl;

    @Singleton
    @Provides
    TokensDataSource provideTokensDataSource(FirebaseDatabaseManager firebaseDatabaseManager, FirebaseAuthenticationManager firebaseAuthenticationManager) {
        if (null == mTokensDataSourceImpl)
            mTokensDataSourceImpl = new TokensDataSourceImpl(firebaseDatabaseManager, firebaseAuthenticationManager);
        return mTokensDataSourceImpl;
    }
}
