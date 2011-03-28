package se.ekonomipuls;

import android.app.ProgressDialog;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Michael Svensson
 */

//TODO: Might get rid of this
public class ProgressDialogProvider implements Provider<ProgressDialog> {

    @Inject
    private Context context;

    @Override
    public ProgressDialog get() {
        return new ProgressDialog(context);
    }

} 