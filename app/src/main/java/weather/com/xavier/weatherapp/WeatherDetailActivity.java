package weather.com.xavier.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;
import weather.com.xavier.weatherapp.databinding.ActivityWeatherdetailBinding;
import weather.com.xavier.weatherapp.networking.OpenWeatherMapService;
import weather.com.xavier.weatherapp.object.OpenWeatherMapResponse;
import weather.com.xavier.weatherapp.object.WeatherRealmObject;

public class WeatherDetailActivity extends AppCompatActivity implements WeatherAdapter.OnClick {
    public static final String CITY_ID_KEY = "CITY_ID_KEY";

    private ActivityWeatherdetailBinding    mWeatherBinding;
    private WeatherAdapter                  mWeatherAdapter;
    private OpenWeatherMapService           mWeatherApi;
    private List<WeatherRealmObject>        mWeatherList = new ArrayList<>();
    private int                             mCityId;

    private Realm                           mRealmInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCityId = getIntent().getIntExtra(CITY_ID_KEY, 0);

        //region Create an instance
        mRealmInstance = Realm.getDefaultInstance();
        //endregion

        mWeatherBinding = DataBindingUtil.setContentView(this, R.layout.activity_weatherdetail);

        //region API call
        /** Need to set permit if were to call API in the main thread */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mWeatherApi = RetrofitClient.getClient().create(OpenWeatherMapService.class);

        getWeather();
        //endregion

        //region Set up Adapter
        mWeatherList = mRealmInstance.copyFromRealm(readFromRealm());
        mWeatherAdapter = new WeatherAdapter(this, mWeatherList, this);
        mWeatherBinding.recyclerView.setAdapter(mWeatherAdapter);
        mWeatherBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //endregion
    }

    @Override
    protected void onDestroy() {
        //region End realm after closing app
        mRealmInstance.close();
        //endregion

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);

        MenuItem menuItem = menu.findItem(R.id.action_menu);
        if (menuItem != null) {
            menuItem.setIcon(R.drawable.ic_add);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.COUNTRY_ADDED_KEY, true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWeatherClicked(WeatherRealmObject weatherRealmObject) {
        buildAlertMessageDelete(weatherRealmObject);
    }

    private void getWeather() {
        try {
            Response<OpenWeatherMapResponse> response = mWeatherApi.getWeather(mCityId, BuildConfig.API_KEY, "metric").execute();

            if (response.isSuccessful() && response.body() != null) {

                OpenWeatherMapResponse weatherResponse = response.body();

                if (weatherResponse != null) {
                    mWeatherBinding.country.setText(weatherResponse.name);
                    if (!weatherResponse.weather.isEmpty()) {
                        //region Set the weather type
                        mWeatherBinding.weatherType.setText(weatherResponse.weather.get(0).main);
                        //endregion

                        //region Set the weather description
                        mWeatherBinding.weatherDescription.setText(weatherResponse.weather.get(0).description);
                        //endregion

                        //region Weather Condition @ https://openweathermap.org/weather-conditions
                        mWeatherBinding.imgMainweather.setImageDrawable(weatherResponse.weather.get(0).getIcon(this));
                        //endregion
                    }
                    //region Set the Temperature in Celsius (NOTE: unable to display Fahrenheit because it would require calling the endpoint again, with different filter)
                    mWeatherBinding.temperature.setText(weatherResponse.main.temp + "℃");
                    //endregion

                    //region Save this data inside realm
                    writeToRealm(new WeatherRealmObject(weatherResponse.weather.get(0).id, weatherResponse.name, weatherResponse.weather.get(0).main));
                    //endregion
                }

            } else {
                Toast.makeText(this, "API call Fail", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildAlertMessageDelete(WeatherRealmObject weatherRealmObject) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove " + weatherRealmObject.name + " ?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        //region Delete from Realm
                        deleteFromRealm(weatherRealmObject);
                        //endregion

                        //region Update the Weather list after deleting from Realm
                        if (!mWeatherList.isEmpty()) {
                            mWeatherList.clear();
                        }
                        mWeatherList.addAll(mRealmInstance.copyFromRealm(readFromRealm()));
                        mWeatherAdapter.notifyDataSetChanged();
                        //endregion
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void writeToRealm(WeatherRealmObject weatherRealmObject) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(transactionRealm -> {
            WeatherRealmObject weatherRealm = transactionRealm.where(WeatherRealmObject.class)
                                                            .equalTo("name", weatherRealmObject.name)
                                                            .findFirst();

            //region Only remove duplicates when realm instance has lookup object inserted
            if (weatherRealm != null) {
                transactionRealm.where(WeatherRealmObject.class)
                        .equalTo("name", weatherRealmObject.name)
                        .findAll()
                        .deleteAllFromRealm();
                transactionRealm.copyToRealmOrUpdate(weatherRealmObject);
            }
            //endregion

            transactionRealm.copyToRealmOrUpdate(weatherRealmObject);
        });

        realm.close();
    }

    private RealmResults<WeatherRealmObject> readFromRealm() {
        return mRealmInstance.where(WeatherRealmObject.class).findAll();
    }

    private void deleteFromRealm(WeatherRealmObject weatherRealmObject) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(transactionRealm -> {
            transactionRealm.where(WeatherRealmObject.class)
                            .equalTo("name", weatherRealmObject.name)
                            .findAll()
                            .deleteAllFromRealm();
        });

        realm.close();
    }
}
