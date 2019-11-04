package co.id.ramadanrizky.todofirebasefix3.view_model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import co.id.ramadanrizky.todofirebasefix3.pojo.WeatherForecast;
import cz.msebera.android.httpclient.Header;

public class ForecastViewModel extends ViewModel {

    private static final String APP_ID = "d0d7a0d307b90d0f9cc936e98ce50667";
    private MutableLiveData<ArrayList<WeatherForecast>> listForecast = new MutableLiveData<>();

    public void getForecastWeather(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        database.getReference("users").child(user.getUid()).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AsyncHttpClient client = new AsyncHttpClient();
                final ArrayList<WeatherForecast>forecasts = new ArrayList<>();
                String url = "https://api.openweathermap.org/data/2.5/forecast?q="+dataSnapshot.getValue()+"&appid="+APP_ID+"&units=metric&cnt=30";

                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray list = object.getJSONArray("list");

                            for (int i = 0;i<list.length();i++){
                                JSONObject jsObject = list.getJSONObject(i);
                                WeatherForecast weatherForecast = new WeatherForecast(jsObject);
                                forecasts.add(weatherForecast);
                            }
                            listForecast.postValue(forecasts);

                        }catch (JSONException e){
                            Log.d("EXCEP", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("ONFAIL" , error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public LiveData<ArrayList<WeatherForecast>> getForecast(){
        return listForecast;
    }

}
