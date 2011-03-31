package se.ekonomipuls;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * @author Michael Svensson
 */
@RunWith(RobolectricTestRunner.class)
public class EkonomipulsHomeTest {

	private EkonomipulsHome homeActivity;

	@Before
	public void setup() {
		homeActivity = new EkonomipulsHome();
	}

	@Test
	public void checkAppName() throws Exception {
		final String appName = homeActivity.getResources()
				.getString(R.string.app_name);
		assertThat(appName, equalTo("Ekonomipuls"));
	}

}
