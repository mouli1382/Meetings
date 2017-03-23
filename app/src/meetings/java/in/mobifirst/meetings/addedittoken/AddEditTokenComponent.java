package in.mobifirst.meetings.addedittoken;

import dagger.Component;
import in.mobifirst.meetings.application.ApplicationComponent;
import in.mobifirst.meetings.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class,
        modules = AddEditTokenPresenterModule.class)
public interface AddEditTokenComponent {

    void inject(AddEditTokenActivity addEditTokenActivity);
}
