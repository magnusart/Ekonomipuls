package se.ekonomipuls;

import com.google.inject.Module;
import roboguice.application.RoboApplication;

import java.util.List;

/**
 * RoboApplication entry point
 *
 * @author Michael Svensson
 */
public class EkonomipulsApplication extends RoboApplication {

    private Module module = new EkonomipulsModule();

    protected void addApplicationModules(List<Module> modules) {
        modules.add(module);
    }

}
