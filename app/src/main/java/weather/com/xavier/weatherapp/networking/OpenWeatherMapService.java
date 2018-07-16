package weather.com.xavier.weatherapp.networking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import weather.com.xavier.weatherapp.object.OpenWeatherMapResponse;

public interface OpenWeatherMapService {

    @GET("weather")
    Call<OpenWeatherMapResponse> getWeather(@Query("id") int cityid,
                                            @Query("appid") String appid,
                                            @Query("units") String units);

//    @GET("users")
//    Call<List<UserObject>> users(@Query("since") int id,
//                                 @Query("per_page") int perPage);
//
//    @GET("users/{user}")
//    Call<UserObject> userDetail(@Path("user") String user);
}
