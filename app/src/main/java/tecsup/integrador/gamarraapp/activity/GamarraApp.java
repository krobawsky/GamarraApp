package tecsup.integrador.gamarraapp.activity;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.orm.SugarContext;

public class GamarraApp extends Application  {

    @Override
    public void onCreate(){
        super.onCreate();
        SugarContext.init(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}
