package weather.com.xavier.weatherapp.object;

import java.util.List;

public class OpenWeatherMapResponse {
    public List<WeatherObject>  weather;
    public TemperatureObject    main;
    public int                  id;
    public String               name;

    public OpenWeatherMapResponse() {
    }
}
