package co.id.ramadanrizky.todofirebasefix3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import co.id.ramadanrizky.todofirebasefix3.pojo.UserTodo;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txt_login;
    private EditText edt_email_register, edt_pass_register, edt_address_register;
    private Button btn_register;
    private DatabaseReference database;
    private FirebaseAuth fAuth;
    private AVLoadingIndicatorView pb;
    private LinearLayout linepb;
    SharedPreferencesManager prefm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBarBackground));
        }else {
            setTheme(R.style.ActivityTheme_Light);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_register);

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);

        initView();
        setListener();



    }

    private void setListener() {
        txt_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    private void initView() {
        txt_login = findViewById(R.id.txt_login);
        database = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        edt_email_register = findViewById(R.id.txt_email_register);
        edt_pass_register = findViewById(R.id.txt_password_register);
        edt_address_register = findViewById(R.id.txt_address_register);
        btn_register = findViewById(R.id.btn_register);
        linepb = findViewById(R.id.linePb);
        pb = findViewById(R.id.pb);


    }

    @Override
    public void onClick(View view) {
        if (view == btn_register){
            register();

        }else if (view == txt_login){
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    }

    private void register() {

        pb.setVisibility(View.VISIBLE);
        linepb.setVisibility(View.VISIBLE);

        Log.d("RegisterActivity", "Registering");
        if (!validateForm()){
            pb.setVisibility(View.GONE);
            linepb.setVisibility(View.GONE);
            return;

        }

        final String email = edt_email_register.getText().toString();
        String password = edt_pass_register.getText().toString();
        final String address = edt_address_register.getText().toString();

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("RegisterActivity", "register complete : "+task.isSuccessful());

                        if (task.isSuccessful()){
                            onSuccessCreate(task.getResult().getUser(), address);
                        }else {
                            Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            linepb.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void onSuccessCreate(FirebaseUser user, String address) {

        String username = usernameFromEmail(user.getEmail());
        String imageUrl = "default";
        makeNewUser(user.getUid(), username, user.getEmail(), address, imageUrl);
        prefm.setKeyStatusLoggedIn(true);
        pb.setVisibility(View.GONE);
        linepb.setVisibility(View.GONE);
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();

    }

    private void makeNewUser(String uid, String username, String email, String address, String imageUrl) {
        UserTodo userTodo = new UserTodo(uid, username, email, address, imageUrl, 0);

        database.child("users").child(uid).setValue(userTodo);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")){
            return email.split("@")[0];
        }else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(edt_email_register.getText().toString())){
            edt_email_register.setError("Email Required!");
            result = false;
            pb.setVisibility(View.GONE);
            linepb.setVisibility(View.GONE);
        }else {
            edt_email_register.setError(null);
        }

        if (TextUtils.isEmpty(edt_pass_register.getText().toString()) || edt_pass_register.getText().length() < 8){
            edt_pass_register.setError("Password Required! and must 8 character");
            result = false;
            pb.setVisibility(View.GONE);
            linepb.setVisibility(View.GONE);
        }else {
            edt_pass_register.setError(null);
        }


        if (TextUtils.isEmpty(edt_address_register.getText().toString())){
            edt_address_register.setError("Address Required!");
            result = false;
            pb.setVisibility(View.GONE);
            linepb.setVisibility(View.GONE);
        }else {
            edt_address_register.setError(null);
        }

        return result;
    }
}
