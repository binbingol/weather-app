package weather.com.xavier.weatherapp;

import android.app.Application;

import io.realm.Realm;

public class BaseApplicantion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //region Initialize Realm
        Realm.init(this);
        //endregion
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
