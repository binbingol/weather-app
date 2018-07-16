package weather.com.xavier.weatherapp.object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CityObject extends RealmObject {

    @PrimaryKey
    public int      id;
    public String   name;
    public String   country;

    public CityObject() {
    }
}
