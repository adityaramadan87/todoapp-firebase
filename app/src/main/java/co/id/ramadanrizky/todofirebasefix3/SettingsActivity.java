package co.id.ramadanrizky.todofirebasefix3;

import androidx.appcompat.app.AppCompatActivity;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch switchNight;
    private SharedPreferencesManager prefm;
    private TextView txt_rate_us, txt_about, txt_about_2, txt_version;
    private ImageView img_back;
    private LinearLayout layout_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
        }else {
            setTheme(R.style.ActivityTheme_Light);
        }
        setContentView(R.layout.activity_settings);

        initView();
        listener();
        loadNightMode();
        setTransparentStatusBar();
        switchChecked();
        setVersion();
    }

    @SuppressLint("SetTextI18n")
    private void setVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            txt_version.setText("v"+versionName+"("+versionNumber+")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void listener() {
        txt_about.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        img_back.setOnClickListener(this);
        txt_rate_us.setOnClickListener(this);
    }

    private void initView() {
        switchNight         = (Switch) findViewById(R.id.switchNight);
        img_back            = findViewById(R.id.imgBack);
        txt_about           = findViewById(R.id.txt_about_app);
        txt_about_2         = findViewById(R.id.txt_about_2);
        txt_rate_us         = findViewById(R.id.txt_rate_us);
        layout_about        = findViewById(R.id.layout_about);
        txt_version         = findViewById(R.id.txt_version);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void switchChecked() {
        switchNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    prefm.saveNightModeState(true);
                    restartApp();
                }else {
                    prefm.saveNightModeState(false);
                    restartApp();
                }
            }
        });

    }

    private void restartApp() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();



    }

    private void setTransparentStatusBar() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    private void loadNightMode() {
        if (prefm.loadNightModeState()==true){
            switchNight.setChecked(true);
            txt_about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android_white_24dp,0,0,0);
            txt_about_2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android_white_24dp,0,0,0);
            txt_rate_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_white_24dp,0,0,0);

        }else {
            switchNight.setChecked(false);
            txt_about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android_black_24dp,0,0,0);
            txt_about_2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android_black_24dp,0,0,0);
            txt_rate_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_24dp,0,0,0);

        }
    }

    @Override
    public void onClick(View view) {
        if (view == txt_about){
            layout_about.setVisibility(View.VISIBLE);
        }else if (view == layout_about){
            layout_about.setVisibility(View.GONE);
        }else if (view == img_back){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else if (view == txt_rate_us){
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName)));
            }catch (ActivityNotFoundException anfe){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName)));
            }
        }
    }


}
