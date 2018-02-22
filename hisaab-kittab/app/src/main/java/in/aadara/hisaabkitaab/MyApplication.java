package in.aadara.hisaabkitaab;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import in.aadara.hisaabkitaab.util.TypefaceUtil;

/**
 * Created by umashankarpathak on 15/01/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TypefaceUtil.overrideFont(getApplicationContext(), "ROBOTO", "roboto_light.ttf");

    }
}
