package weather.com.xavier.weatherapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import weather.com.xavier.weatherapp.databinding.ItemWeatherBinding;
import weather.com.xavier.weatherapp.object.WeatherRealmObject;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private Context                     mContext;
    private List<WeatherRealmObject>    mWeatherList;
    private OnClick                     mOnClickListener;

    public WeatherAdapter(Context context, List<WeatherRealmObject> list, OnClick listener) {
        this.mContext = context;
        this.mWeatherList = list;
        this.mOnClickListener = listener;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemWeatherBinding weatherBinding = DataBindingUtil.inflate(inflater, R.layout.item_weather, parent, false);
        return new WeatherViewHolder(weatherBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherRealmObject weatherRealmObject = mWeatherList.get(position);
        holder.bind(weatherRealmObject);

        //region Weather Condition @ https://openweathermap.org/weather-conditions
        holder.weatherBinding.imgWeather.setImageDrawable(weatherRealmObject.getIcon(mContext));
        //endregion

        //region Set on CLick Listener to handle click events
        holder.itemView.getRootView().setOnClickListener(view -> mOnClickListener.onWeatherClicked(weatherRealmObject));
        //endregion
    }

    @Override
    public int getItemCount() {
        return mWeatherList != null ? mWeatherList.size() : 0;
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ItemWeatherBinding  weatherBinding;

        WeatherViewHolder(ItemWeatherBinding binding) {
            super(binding.getRoot());
            this.weatherBinding = binding;
        }

        void bind(WeatherRealmObject weatherRealmObject) {
            this.weatherBinding.setObj(weatherRealmObject);
            this.weatherBinding.executePendingBindings();
        }
    }

    public interface OnClick {
        void onWeatherClicked(WeatherRealmObject weatherRealmObject);
    }
}
