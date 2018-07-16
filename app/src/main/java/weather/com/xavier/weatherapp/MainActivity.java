package weather.com.xavier.weatherapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;
import weather.com.xavier.weatherapp.databinding.ActivityMainBinding;
import weather.com.xavier.weatherapp.object.CityObject;

public class MainActivity extends AppCompatActivity implements CityAdapter.OnClick {
    public static final String  COUNTRY_ADDED_KEY = "COUNTRY_ADDED_KEY";

    public ObservableBoolean    isSuggestionVisible = new ObservableBoolean(false);

    private boolean             isCountryAdded = false;
    private Realm               mRealmInstance;

    private CityAdapter         mCityAdapter;

    private List<CityObject>    mSuggestedCityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isCountryAdded = getIntent().getBooleanExtra(COUNTRY_ADDED_KEY, false);

        //region Create an instance
        mRealmInstance = Realm.getDefaultInstance();
        //endregion

        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setIsSuggestionVisible(isSuggestionVisible);

        //region Set up Adapter
        mCityAdapter = new CityAdapter(mSuggestedCityList, this);
        mainBinding.recyclerView.setAdapter(mCityAdapter);
        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        //region Detect editText ui change
        mainBinding.editText.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer = new Timer();
                    private final long DELAY = 500; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // TODO: do what you need here (refresh list)
                                    runOnUiThread(() -> {
                                        if (s.toString().isEmpty()) {
                                            mSuggestedCityList.clear();
                                        } else {
                                            RealmResults<CityObject> realmresults = readFromRealm(s.toString());
                                            if (!mSuggestedCityList.isEmpty()) {
                                                mSuggestedCityList.clear();
                                            }
                                            mSuggestedCityList.addAll(mRealmInstance.copyFromRealm(realmresults));
                                        }
                                        isSuggestionVisible.set(!mSuggestedCityList.isEmpty());
                                        mCityAdapter.notifyDataSetChanged();
                                    });
                                }
                            },
                            DELAY
                        );
                    }
                }
        );
        //endregion
    }

    @Override
    protected void onStart() {
        super.onStart();

        //region Import the json file into Realm IF the records does not exist (only one first installation of app
        int citySize = mRealmInstance.where(CityObject.class).findAll().size();
        if (citySize <= 0) {
            RealmImporter.importFromJson(getResources());
        }
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
        if (isCountryAdded) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.actionbar, menu);

            MenuItem menuItem = menu.findItem(R.id.action_menu);
            if (menuItem != null) {
                menuItem.setIcon(R.drawable.ic_close);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCityClicked(CityObject city) {
        Intent intent = new Intent(this, WeatherDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WeatherDetailActivity.CITY_ID_KEY, city.id);
        startActivity(intent);
        finish();
    }

    private RealmResults<CityObject> readFromRealm(String name) {
        return mRealmInstance.where(CityObject.class).contains("name", name).findAll().sort("name");
    }
}
