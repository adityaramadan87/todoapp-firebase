package co.id.ramadanrizky.todofirebasefix3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.id.ramadanrizky.todofirebasefix3.R;
import co.id.ramadanrizky.todofirebasefix3.pojo.WeatherForecast;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private ArrayList<WeatherForecast> forecastItems = new ArrayList<>();

    public void setData(ArrayList<WeatherForecast> forecasts){
        forecastItems.clear();
        forecastItems.addAll(forecasts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        ForecastAdapter.ForecastViewHolder forecastViewHolder = new ForecastAdapter.ForecastViewHolder(v);
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        holder.bind(forecastItems.get(position));
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_forecast;
        private TextView txt_temp, txt_date, txt_desc;
        private SimpleDateFormat sdf;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);

            img_forecast = itemView.findViewById(R.id.img_forecast);
            txt_temp = itemView.findViewById(R.id.txt_temp);
            txt_date = itemView.findViewById(R.id.txt_time_forecast);
            txt_desc = itemView.findViewById(R.id.txt_desc_weather_forecast);
            this.sdf = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());

        }

        public void bind(WeatherForecast weatherForecast) {
//            String date = sdf.format(weatherForecast.getDate());
            String cuaca = weatherForecast.getIcon();
            txt_temp.setText(weatherForecast.getTemperature()+"°C");
            txt_desc.setText(weatherForecast.getDescription());
            txt_date.setText(convertUnixToDate(weatherForecast.getDate()));
            if (cuaca.equals("01d")){
                img_forecast.setImageResource(R.drawable.sun);
            }else if (cuaca.equals("01n")){
                img_forecast.setImageResource(R.drawable.moon);
            }else if (cuaca.equals("02d")){
                img_forecast.setImageResource(R.drawable.cloudy);
            }else if (cuaca.equals("02n")){
                img_forecast.setImageResource(R.drawable.cloudy_night);
            }else if (cuaca.equals("03d")){
                img_forecast.setImageResource(R.drawable.clouds);
            }else if (cuaca.equals("03n")){
                img_forecast.setImageResource(R.drawable.clouds);
            }else if (cuaca.equals("04d")){
                img_forecast.setImageResource(R.drawable.cloudy);
            }else if (cuaca.equals("04n")){
                img_forecast.setImageResource(R.drawable.cloudy);
            }else if (cuaca.equals("09d")){
                img_forecast.setImageResource(R.drawable.rain);
            }else if (cuaca.equals("09n")){
                img_forecast.setImageResource(R.drawable.rain);
            }else if (cuaca.equals("10d")){
                img_forecast.setImageResource(R.drawable.sun_rainy);
            }else if (cuaca.equals("10n")){
                img_forecast.setImageResource(R.drawable.rain);
            }else if (cuaca.equals("11d")){
                img_forecast.setImageResource(R.drawable.day_storm);
            }else if (cuaca.equals("11n")){
                img_forecast.setImageResource(R.drawable.night_thunder);
            }else if (cuaca.equals("13d")){
                img_forecast.setImageResource(R.drawable.snowy);
            }else if (cuaca.equals("13n")){
                img_forecast.setImageResource(R.drawable.night_snow);
            }else if (cuaca.equals("50d")){
                img_forecast.setImageResource(R.drawable.foggy);
            }else if (cuaca.equals("50n")){
                img_forecast.setImageResource(R.drawable.foggy);
            }

        }
    }

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
