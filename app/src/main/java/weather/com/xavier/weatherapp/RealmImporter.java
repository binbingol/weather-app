package weather.com.xavier.weatherapp;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;

import io.realm.Realm;
import weather.com.xavier.weatherapp.object.CityObject;

public class RealmImporter {

    static void importFromJson(final Resources resources) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(transactionRealm -> {
            try {
                InputStream inputStream = resources.openRawResource(R.raw.citylist);
                transactionRealm.createAllFromJson(CityObject.class, inputStream);
//                transactionRealm.createOrUpdateAllFromJson(CityObject.class, inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        realm.close();
    }
}
