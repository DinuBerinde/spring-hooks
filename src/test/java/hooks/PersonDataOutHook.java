package hooks;

import com.dinuberinde.hooks.Hook;
import helper.DataHolder;
import helper.Person;

public class PersonDataOutHook {

    public void dataOut(Hook hook) {
        Person person = (Person) hook.getDataOut();

        DataHolder.map.put(PersonDataOutHook.class.getName(), new DataHolder.Logger(hook.getTag(), person.getName()));
    }
}
