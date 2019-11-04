package co.id.ramadanrizky.todofirebasefix3.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import co.id.ramadanrizky.todofirebasefix3.R;
import co.id.ramadanrizky.todofirebasefix3.WeatherActivity;
import co.id.ramadanrizky.todofirebasefix3.adapter.TodoAdapter;
import co.id.ramadanrizky.todofirebasefix3.grid_decoration.GridSpacingItemDecoration;
import co.id.ramadanrizky.todofirebasefix3.pojo.TodoItems;
import co.id.ramadanrizky.todofirebasefix3.pojo.UserTodo;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ImageView img_weather, img_empty_task;
    RecyclerView rvgrid;
    ArrayList<TodoItems> todoItemsArrayList;
    TodoAdapter adapter;
    ArrayList<UserTodo> userTodos;
    AVLoadingIndicatorView pb;
    SharedPreferencesManager prefm;
    TextView txt_title, txt_empty_task;
    LinearLayout linepb;
    AdView adView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        pb  = v.findViewById(R.id.pb);
        rvgrid = v.findViewById(R.id.gridView);
        linepb = v.findViewById(R.id.linePb);
        linepb.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);
        txt_title = v.findViewById(R.id.txtTitle);
        img_empty_task = v.findViewById(R.id.img_empty_task);
        txt_empty_task = v.findViewById(R.id.txt_empty_task);
        adView = v.findViewById(R.id.adView);

        getWeather(v);
        setUsername();

        prefm = new SharedPreferencesManager(getContext());

        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        return v;
    }

    private void setUsername() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        database.getReference("users").child(user.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.getValue();
                String usernameCapitalize = username.substring(0,1).toUpperCase() + username.substring(1);

                if (hourOfDay >= 4 && hourOfDay < 12){
                    txt_title.setText("Good Morning ,\n" + usernameCapitalize + " :)");
                }else if (hourOfDay >= 12 && hourOfDay < 15){
                    txt_title.setText("Good Afternoon ,\n" + usernameCapitalize + " :)");
                }else if (hourOfDay >= 15 && hourOfDay < 19){
                    txt_title.setText("Good Evening ,\n" + usernameCapitalize + " :)");
                }else if (hourOfDay >= 19 && hourOfDay < 4){
                    txt_title.setText("Good Night ,\n" + usernameCapitalize + " :)");
                }


                setupRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getWeather(View v) {
        img_weather = v.findViewById(R.id.imgweather);
        userTodos = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        database.getReference("users").child(user.getUid()).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = "http://api.openweathermap.org/data/2.5/weather?q="+dataSnapshot.getValue()+"&APPID=d0d7a0d307b90d0f9cc936e98ce50667&units=metric";

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray weather = object.getJSONArray("weather");

                            for (int i = 0;i<weather.length();i++){
                                JSONObject jsonObject = weather.getJSONObject(i);
                                String cuaca = jsonObject.getString("icon");
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
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        img_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), WeatherActivity.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        todoItemsArrayList = new ArrayList<>();
        adapter = new TodoAdapter(getContext(), todoItemsArrayList);

    }



    private void setupRecycler() {


//            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            rvgrid.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
            rvgrid.setItemAnimator(new DefaultItemAnimator());
            rvgrid.setLayoutManager(staggeredGridLayoutManager);

            rvgrid.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            pb.setVisibility(View.GONE);
            linepb.setVisibility(View.GONE);

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkEmpty(adapter);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    checkEmpty(adapter);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    checkEmpty(adapter);
                }
            });

    }

//    private void adView() {
//
//        TodoItems banner = new TodoItems();
//        banner.setViewType(2);
//        todoItemsArrayList.add(1,banner);
//
////        int interval = 10;
////        for (int i=0;i<(todoItemsArrayList.size()/interval);i++){
////            int position=((i+1)*(interval+1))+3;
////            if (position<todoItemsArrayList.size()){
////                todoItemsArrayList.add(position, banner);
////            }
////        }
//    }


    private void checkEmpty(final TodoAdapter adapter) {
        img_empty_task.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        txt_empty_task.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (adRequest != null){
            adView.loadAd(adRequest);
        }
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private int dpToPx(int dp) {
        Resources resources = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(fuser.getUid()).child("todoList").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoItemsArrayList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    TodoItems todoItems = snapshot.getValue(TodoItems.class);
                    todoItems.setKey(snapshot.getKey());
                    todoItems.setViewType(1);
                    todoItemsArrayList.add(todoItems);

//                    adView();
                }
                adapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
                linepb.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setUsername();
    }
}
