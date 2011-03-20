package se.ekonomipuls;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Michael Svensson
 */
@RunWith(RobolectricTestRunner.class)
public class EkonomipulsHomeTest {

    @Test
    public void checkAppName() throws Exception {
        String appName = new EkonomipulsHome().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("Ekonomipuls"));
    }
}
