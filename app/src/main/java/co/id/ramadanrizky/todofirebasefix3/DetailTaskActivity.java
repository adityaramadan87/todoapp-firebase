package co.id.ramadanrizky.todofirebasefix3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import co.id.ramadanrizky.todofirebasefix3.pojo.TodoItems;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;
import co.id.ramadanrizky.todofirebasefix3.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class DetailTaskActivity extends AppCompatActivity {

    TextView txt_edit, txt_category;
    EditText edt_task, edt_datePicker;
    Spinner spinner;
    Button btn_done;
    ImageView loyalty, img_back;
    DatabaseReference databaseReference;
    TodoItems items;
    SimpleDateFormat sdf;


    static final String[] category = new String[] {"Important", "Normal"};
    SharedPreferencesManager prefm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
        }else {
            setTheme(R.style.ActivityTheme_Light);
        }
//        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_detail_task);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
        initView();
        listener();
        getData();
    }



    private void initView() {
        txt_edit = findViewById(R.id.txt_edit);
        edt_task = findViewById(R.id.edt_task);
        edt_datePicker = findViewById(R.id.txt_datepicker);
        spinner = findViewById(R.id.spinner);
        btn_done                = findViewById(R.id.btn_createtask);
        txt_category            = findViewById(R.id.txt_category);
        loyalty                 = findViewById(R.id.img_loyalty);
        img_back                = findViewById(R.id.imgBack);
        this.sdf                = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());
        databaseReference       = FirebaseDatabase.getInstance().getReference();

        edt_task.setEnabled(false);
        edt_datePicker.setEnabled(false);
        spinner.setEnabled(false);

        setAdapterSpinner();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //onClick method
    private void listener() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_task.setEnabled(true);
                edt_datePicker.setEnabled(true);
                spinner.setEnabled(true);
                btn_done.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                txt_category.setVisibility(View.GONE);
                txt_category.setHeight(0);
                loyalty.setVisibility(View.VISIBLE);

            }
        });

        edt_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
    }

    private void datePicker() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .title("Date Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        String date1 = sdf.format(date);
                        edt_datePicker.setText(date1);
                    }
                }).display();
    }

    private void setAdapterSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, category);
        spinner.setAdapter(arrayAdapter);
    }

    private void getData() {
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            items = (TodoItems) extras.get("todo");
            if (items != null) {
                edt_task.setText(items.getTask());
                txt_category.setText(items.getCategory());
                edt_datePicker.setText(items.getDate());
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String task = edt_task.getText().toString();
                        String date = edt_datePicker.getText().toString();
                        String category = spinner.getSelectedItem().toString();

                        items.setTask(task);
                        items.setDate(date);
                        items.setCategory(category);

                        if (!isEmpty(task) && !isEmpty(date) && !isEmpty(category)){
                            updateTask(items);
                            onBackPressed();
                            finish();
                        }else {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.btn_createtask), "can't update empty data", Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            snackbar.show();
                        }

                    }
                });
            }
        }
    }

    private void updateTask(TodoItems items) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("users").child(user.getUid()).child("todoList")
                .child(items.getKey())
                .setValue(items)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailTaskActivity.this, "Data Updated :)", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
