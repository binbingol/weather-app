package weather.com.xavier.weatherapp.object;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import weather.com.xavier.weatherapp.R;

public class WeatherRealmObject extends RealmObject {
    @PrimaryKey
    public String name;

    public int weather_id;
    public String weather_type;

    public WeatherRealmObject() {
    }

    public WeatherRealmObject(int weatherId, String name, String weather) {
        this.weather_id = weatherId;
        this.name = name;
        this.weather_type = weather;
    }

    public Drawable getIcon(Context context) {
        int firstDigit = Integer.parseInt(Integer.toString(weather_id).substring(0, 1));

        //region Weather Condition @ https://openweathermap.org/weather-conditions
        switch (firstDigit) {
            case 2:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_thunderstorm, null);

            case 3:
            case 5:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_rain, null);

            case 6:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_snow, null);

            case 8:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_cloudy, null);
        }

        switch (weather_id) {
            case 741:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_fog, null);

            case 781:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_tornado, null);

            case 800:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_sun, null);
        }
        //endregion

        return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_suncloud, null);
    }
}
