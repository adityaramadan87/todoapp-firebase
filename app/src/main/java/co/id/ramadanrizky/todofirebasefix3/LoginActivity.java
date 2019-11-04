package co.id.ramadanrizky.todofirebasefix3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import co.id.ramadanrizky.todofirebasefix3.pojo.UserTodo;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txt_register, txt_forgot_pass, txt_instruc_email;
    private EditText edt_email_login, edt_password_login;
    private Button btn_login;
    private FirebaseAuth fAuth;
    private LinearLayout linePb;
    AVLoadingIndicatorView pb;
    SharedPreferencesManager prefm;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    View dialView;
    GoogleSignInClient googleSignInClient;
    DatabaseReference reference;
    SignInButton googleButton;
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onStart() {
        super.onStart();
        if (prefm.getKeyStatusLoggedIn()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
    
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
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);

        initView();
        setListener();
        signWithGoogle();

    }

    private void signWithGoogle() {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }


    private void setListener() {

        txt_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);
        googleButton.setOnClickListener(this);

    }

    private void initView() {

        reference = FirebaseDatabase.getInstance().getReference();
        txt_register = findViewById(R.id.txt_register);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);
        edt_email_login = findViewById(R.id.txt_email_login);
        googleButton = (SignInButton) findViewById(R.id.google_button); 
        edt_password_login = findViewById(R.id.txt_password_login);
        btn_login = findViewById(R.id.btn_login);
        inflater        = getLayoutInflater();
        dialView           = inflater.inflate(R.layout.forgot_password_form, null);
        txt_instruc_email = dialView.findViewById(R.id.txt_instruction_email);
        pb = findViewById(R.id.pb);
        linePb = findViewById(R.id.linePb);
        fAuth = FirebaseAuth.getInstance();

    }

    

    @Override
    public void onClick(View view) {

        if (view == txt_register){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }else if (view == btn_login){
            login();

        }else if (view == txt_forgot_pass){
            showAlertDialog();
        }else if (view == googleButton){
            loginUsingGoogle();
        }

    }

    private void loginUsingGoogle() {
        Intent signIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null){
                    firebaseAuthUseGoogle(account);
                    pb.setVisibility(View.VISIBLE);
                    linePb.setVisibility(View.VISIBLE);
                }else {
                    pb.setVisibility(View.GONE);
                    linePb.setVisibility(View.GONE);
                }
                
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void firebaseAuthUseGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUi(user);
                            pb.setVisibility(View.GONE);
                            linePb.setVisibility(View.GONE);


                        }else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            updateUi(null);
                        }
                    }
                });

    }

    private void updateUi(FirebaseUser user) {

        if (user != null){
            String username = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();
            String imgURL = "default";
            String address = "Jakarta";

            prefm.setKeyStatusLoggedIn(true);
            makeNewUser(uid, username, email, address, imgURL);

        }

    }


    private void makeNewUser(String uid, String username, String email, String address, String imgURL) {

        UserTodo userTodo = new UserTodo(uid, username, email, address, imgURL, 1);

        reference.child("users").child(uid).setValue(userTodo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String email = edt_email_login.getText().toString();

        if (TextUtils.isEmpty(email)){
            edt_email_login.setError("Email Required for send New Password");
        }else {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                builder = new AlertDialog.Builder(LoginActivity.this);

                                if (dialView.getParent()!= null){
                                    ((ViewGroup)dialView.getParent()).removeView(dialView);
                                }
                                builder.setView(dialView);
                                builder.setCancelable(true);
                                txt_instruc_email.setText("Reset password instructions has sent to email "+email);

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                                builder.show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Email Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void login() {


        pb.setVisibility(View.VISIBLE);
        linePb.setVisibility(View.VISIBLE);

        Log.d("LOGIN Activity", "login");
        if (!validateForm()){
            pb.setVisibility(View.GONE);
            linePb.setVisibility(View.GONE);
            return;
        }

        String email = edt_email_login.getText().toString();
        String password = edt_password_login.getText().toString();

        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LoginActivity", "login complete :" + task.isSuccessful());

                        if (task.isSuccessful()){
                            prefm.setKeyStatusLoggedIn(true);
                            pb.setVisibility(View.GONE);
                            linePb.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else {
                            pb.setVisibility(View.GONE);
                            linePb.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Failed, check email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(edt_email_login.getText().toString())){
            edt_email_login.setError("Email Required!");
            result = false;
            pb.setVisibility(View.GONE);
            linePb.setVisibility(View.GONE);
        }else {
            edt_email_login.setError(null);
        }


        if (TextUtils.isEmpty(edt_password_login.getText().toString()) || edt_password_login.getText().length() < 8){
            edt_password_login.setError("Password Required and must 8 character");
            result = false;
            pb.setVisibility(View.GONE);
            linePb.setVisibility(View.GONE);
        }else {
            edt_password_login.setError(null);
        }

        return result;
    }
}
