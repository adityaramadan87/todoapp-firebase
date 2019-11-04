package co.id.ramadanrizky.todofirebasefix3;

import androidx.appcompat.app.AppCompatActivity;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private ImageView img_text_logo;
    private Animation animation;
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
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        img_text_logo = findViewById(R.id.img_logo);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img_text_logo.startAnimation(animation);
    }
}
