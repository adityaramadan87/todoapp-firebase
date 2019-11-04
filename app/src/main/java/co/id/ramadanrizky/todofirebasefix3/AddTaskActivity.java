package co.id.ramadanrizky.todofirebasefix3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import co.id.ramadanrizky.todofirebasefix3.broadcast_receiver.NotificationPublish;
import co.id.ramadanrizky.todofirebasefix3.fragment.HomeFragment;
import co.id.ramadanrizky.todofirebasefix3.pojo.TodoItems;
import co.id.ramadanrizky.todofirebasefix3.pojo.UserTodo;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;
import co.id.ramadanrizky.todofirebasefix3.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.text.TextUtils.isEmpty;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_add_task;
    Spinner spinner;
    ImageView img_back;
    EditText edt_datepicker, edt_task;
    SimpleDateFormat sdf;
    String task;
    Calendar calendar;
    SharedPreferencesManager prefm;
    Date dateTime;

    static final String[] category = new String[] {"Important", "Normal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setDarkTheme
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
        }else {
            setTheme(R.style.ActivityTheme_Light);
        }
        setContentView(R.layout.activity_add_task);

        btn_add_task            = findViewById(R.id.btn_createtask);
        spinner                 = findViewById(R.id.spinner);
        edt_task                = findViewById(R.id.edt_task);
        edt_datepicker          = findViewById(R.id.txt_datepicker);
        img_back                = findViewById(R.id.imgBack);

        this.sdf                = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());

        btn_add_task.setOnClickListener(this);
        edt_datepicker.setOnClickListener(this);
        img_back.setOnClickListener(this);

        setAdapterSpinner();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);

    }

    //set adapter for spinner
    private void setAdapterSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, category);
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //onclick
    @Override
    public void onClick(View view) {
        if (view == btn_add_task){
            addTask();
        }else if (view == edt_datepicker){
            datePicker();
        }else if (view == img_back){
            onBackPressed();
            finish();
        }
    }

    private void scheduleNotification(Notification notification, Date calendar) {

        Intent notifIntent = new Intent(this, NotificationPublish.class);
        notifIntent.putExtra(NotificationPublish.NOTIFICATION_ID, 0);
        notifIntent.putExtra(NotificationPublish.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTime(), pendingIntent);
    }

    private Notification getNotification(String task) {

        String channel_id = "CHANNEL_1";
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int argb = Color.argb(1,225,0,0);
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent penTent = PendingIntent.getActivity(getApplicationContext(), 0, notIntent, 0);


        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channel_id, "Channel Schedo", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(false);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.WHITE);
            channel.setVibrationPattern(new long[] {1000, 1000, 1000, 1000, 1000});
            builder = new Notification.Builder(this, channel_id);
            builder.setSmallIcon(R.drawable.ic_check_circle_black_24dp);
            builder.setContentTitle(task);
            builder.setContentText("Go Finish Your Task");
            builder.setContentIntent(penTent);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setColor(ContextCompat.getColor(AddTaskActivity.this, R.color.colorAccent));
            builder.setShowWhen(true);
            notificationManager.createNotificationChannel(channel);
        }else {
            builder = new Notification.Builder(this);
            builder.setContentTitle(task);
            builder.setContentText("Go Finish Your Task");
            builder.setContentIntent(penTent);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setColor(ContextCompat.getColor(AddTaskActivity.this, R.color.colorAccent));
            builder.setSmallIcon(R.drawable.ic_check_circle_black_24dp);
            builder.setVibrate(new long[] {1000, 1000, 1000, 1000, 1000});
            builder.setLights(0xff0000ff, 3000, 3000 );
            builder.setAutoCancel(true);
            builder.setSound(sound);
            builder.setWhen(System.currentTimeMillis());
            builder.setShowWhen(true);
        }
        return builder.build();
    }


    private void addTask() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("users").child(firebaseUser.getUid()).child("todoList").push().getKey();

        task = edt_task.getText().toString();
        String date = edt_datepicker.getText().toString();
        String category = spinner.getSelectedItem().toString();

        TodoItems items = new TodoItems();
        items.setTask(edt_task.getText().toString());
        items.setDate(edt_datepicker.getText().toString());
        items.setCategory(spinner.getSelectedItem().toString());

        if (!isEmpty(task) && !isEmpty(date) && !isEmpty(category)){

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, items.toFirebaseObj());
            database.getReference("users").child(firebaseUser.getUid()).child("todoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null){
                        finish();
                    }
                }
            });
            scheduleNotification(getNotification(task),dateTime);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.btn_createtask), "can't update empty data", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }


    }




    private void datePicker() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .title("Date Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        String date3 = sdf.format(date);
                        dateTime = date;
                        edt_datepicker.setText(date3);
                    }
                }).display();
    }
}








