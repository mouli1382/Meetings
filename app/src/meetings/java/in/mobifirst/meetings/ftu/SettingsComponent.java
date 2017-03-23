package in.mobifirst.meetings.ftu;

import dagger.Component;
import in.mobifirst.meetings.application.ApplicationComponent;
import in.mobifirst.meetings.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SettingsPresenterModule.class)
public interface SettingsComponent {

    void inject(SettingsActivity settingsActivity);
}
