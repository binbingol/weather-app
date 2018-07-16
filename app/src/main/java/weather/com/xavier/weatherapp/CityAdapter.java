package weather.com.xavier.weatherapp;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import weather.com.xavier.weatherapp.databinding.ItemCityBinding;
import weather.com.xavier.weatherapp.object.CityObject;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private List<CityObject>    mCityList;
    private OnClick             mOnClickListener;

    public CityAdapter(List<CityObject> list, OnClick listener) {
        this.mCityList = list;
        this.mOnClickListener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCityBinding cityBinding = DataBindingUtil.inflate(inflater, R.layout.item_city, parent, false);
        return new CityViewHolder(cityBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        CityObject city = mCityList.get(position);
        holder.bind(city);

        //region Set on CLick Listener to handle click events
        holder.itemView.getRootView().setOnClickListener(view -> mOnClickListener.onCityClicked(city));
        //endregion
    }

    @Override
    public int getItemCount() {
        return mCityList != null ? mCityList.size() : 0;
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        ItemCityBinding cityBinding;

        CityViewHolder(ItemCityBinding binding) {
            super(binding.getRoot());
            this.cityBinding = binding;
        }

        void bind(CityObject city) {
            this.cityBinding.setObj(city);
            this.cityBinding.executePendingBindings();
        }
    }

    public interface OnClick {
        void onCityClicked(CityObject city);
    }
}
