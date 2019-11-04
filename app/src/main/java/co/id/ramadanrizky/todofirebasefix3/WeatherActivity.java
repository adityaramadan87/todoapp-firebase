package co.id.ramadanrizky.todofirebasefix3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.id.ramadanrizky.todofirebasefix3.adapter.ForecastAdapter;
import co.id.ramadanrizky.todofirebasefix3.pojo.WeatherForecast;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;
import co.id.ramadanrizky.todofirebasefix3.view_model.ForecastViewModel;
import cz.msebera.android.httpclient.Header;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class WeatherActivity extends AppCompatActivity {

    private ImageView img_weather, img_back;
    private TextView txt_desc_weather, txt_weather,txt_forecast, txt_city_weather;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearLayout;
    private AVLoadingIndicatorView pb;
    private ForecastAdapter forecastAdapter;
    private RecyclerView rv_forecast;
    private ForecastViewModel forecastViewModel;
    private BottomSheetBehavior sheetBehavior;
    private SharedPreferencesManager prefm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
        }else {
            setTheme(R.style.ActivityTheme_Light);
        }
        setContentView(R.layout.activity_weather);


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);

        linearLayout = findViewById(R.id.linePb);
        pb = findViewById(R.id.pb);

        linearLayout.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);

        txt_city_weather = findViewById(R.id.txt_city_weather);
        img_back = findViewById(R.id.imgBack);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getWeather();
        getForecastWeather();
        setBottomSheet();

    }

    @SuppressLint("SetTextI18n")
    private void setBackGround(String cityname) {
        Calendar calendar = Calendar.getInstance();
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        coordinatorLayout = findViewById(R.id.coordinator);

        if (hourOfDay >= 4 && hourOfDay < 12){
            coordinatorLayout.setBackgroundResource(R.drawable.morning);
            txt_city_weather.setText(cityname +" Morning");
        }else if (hourOfDay >= 12 && hourOfDay < 15){
            coordinatorLayout.setBackgroundResource(R.drawable.light);
            txt_city_weather.setText(cityname +" Afternoon");
        }else if (hourOfDay >= 15 && hourOfDay < 19){
            coordinatorLayout.setBackgroundResource(R.drawable.sunset);
            txt_city_weather.setText(cityname +" Evening");
        }else if (hourOfDay >= 19 && hourOfDay < 4){
            coordinatorLayout.setBackgroundResource(R.drawable.night_mode);
            txt_city_weather.setText(cityname +" Night");
        }
    }

    private void setBottomSheet() {
        final View bottomSheet = (View)findViewById(R.id.bottom_weather);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    case BottomSheetBehavior.STATE_DRAGGING: {

                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING:{
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED:{
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN:{

                        BottomSheetBehavior.from(bottomSheet).setHideable(false);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    private void getForecastWeather() {
        rv_forecast = findViewById(R.id.rv_weather);
        txt_forecast = findViewById(R.id.txt_forecast);


        forecastViewModel = ViewModelProviders.of(this).get(ForecastViewModel.class);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_forecast.setLayoutManager(layoutManager);
        forecastAdapter = new ForecastAdapter();
        rv_forecast.setAdapter(forecastAdapter);

        forecastViewModel.getForecast().observe(this, new Observer<ArrayList<WeatherForecast>>() {
            @Override
            public void onChanged(ArrayList<WeatherForecast> forecasts) {
                if (forecasts != null){
                    forecastAdapter.setData(forecasts);
                }
            }
        });
        forecastViewModel.getForecastWeather();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void getWeather() {
        img_weather = findViewById(R.id.img_weather);

        txt_weather = findViewById(R.id.txt_weather);
        txt_desc_weather = findViewById(R.id.txt_desc_weather);
        txt_forecast = findViewById(R.id.txt_forecast);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        database.getReference("users").child(user.getUid()).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String cityname = (String) dataSnapshot.getValue();
                setBackGround(cityname);
                txt_forecast.setText("forecast weather in "+cityname);
                String url = "http://api.openweathermap.org/data/2.5/weather?q="+dataSnapshot.getValue()+"&APPID=d0d7a0d307b90d0f9cc936e98ce50667&units=metric";
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray weather = object.getJSONArray("weather");
                            String temp = object.getJSONObject("main").getString("temp");
                            for (int i = 0;i<weather.length();i++){
                                JSONObject jsObject = weather.getJSONObject(i);
                                String cuaca = jsObject.getString("icon");
                                String desc = jsObject.getString("description");

                                txt_weather.setText(temp);
                                txt_desc_weather.setText(desc);
                                if (cuaca.equals("01d")){
                                    img_weather.setImageResource(R.drawable.sun);
                                }else if (cuaca.equals("01n")){
                                    img_weather.setImageResource(R.drawable.moon);
                                }else if (cuaca.equals("02d")){
                                    img_weather.setImageResource(R.drawable.cloudy);
                                }else if (cuaca.equals("02n")){
                                    img_weather.setImageResource(R.drawable.cloudy_night);
                                }else if (cuaca.equals("03d")){
                                    img_weather.setImageResource(R.drawable.clouds);
                                }else if (cuaca.equals("03n")){
                                    img_weather.setImageResource(R.drawable.clouds);
                                }else if (cuaca.equals("04d")){
                                    img_weather.setImageResource(R.drawable.cloudy);
                                }else if (cuaca.equals("04n")){
                                    img_weather.setImageResource(R.drawable.cloudy);
                                }else if (cuaca.equals("09d")){
                                    img_weather.setImageResource(R.drawable.rain);
                                }else if (cuaca.equals("09n")){
                                    img_weather.setImageResource(R.drawable.rain);
                                }else if (cuaca.equals("10d")){
                                    img_weather.setImageResource(R.drawable.sun_rainy);
                                }else if (cuaca.equals("10n")){
                                    img_weather.setImageResource(R.drawable.rain);
                                }else if (cuaca.equals("11d")){
                                    img_weather.setImageResource(R.drawable.day_storm);
                                }else if (cuaca.equals("11n")){
                                    img_weather.setImageResource(R.drawable.night_thunder);
                                }else if (cuaca.equals("13d")){
                                    img_weather.setImageResource(R.drawable.snowy);
                                }else if (cuaca.equals("13n")){
                                    img_weather.setImageResource(R.drawable.night_snow);
                                }else if (cuaca.equals("50d")){
                                    img_weather.setImageResource(R.drawable.foggy);
                                }else if (cuaca.equals("50n")){
                                    img_weather.setImageResource(R.drawable.foggy);
                                }

                                linearLayout.setVisibility(View.GONE);
                                pb.setVisibility(View.GONE);

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        linearLayout.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
