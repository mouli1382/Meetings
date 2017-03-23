package in.mobifirst.meetings.tokens;


import dagger.Component;
import in.mobifirst.meetings.application.ApplicationComponent;
import in.mobifirst.meetings.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = TokensPresenterModule.class)
public interface TokensComponent {

    void inject(TokensActivity activity);
}
