package co.id.ramadanrizky.todofirebasefix3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import co.id.ramadanrizky.todofirebasefix3.fragment.HomeFragment;
import co.id.ramadanrizky.todofirebasefix3.fragment.ProfileFragment;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;

import android.view.View;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    SharedPreferencesManager prefm;

    BottomNavigationView btmNav;
    FloatingActionButton fab;
    final Fragment frHome = new HomeFragment();
    final Fragment frProfile = new ProfileFragment();
    final FragmentManager manager = getSupportFragmentManager();
    Fragment active = frHome;
    public static final String PROF_TAG = "1";
    public static final String HOME_TAG = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefm = new SharedPreferencesManager(this);
        if (prefm.loadNightModeState() == true){
            setTheme(R.style.ActivityTheme_Dark);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }else {
            setTheme(R.style.ActivityTheme_Light);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBarBackground));
        }
        setContentView(R.layout.activity_main);


        manager.beginTransaction().add(R.id.main_container, frProfile, PROF_TAG).hide(frProfile).commit();
        manager.beginTransaction().add(R.id.main_container, frHome, HOME_TAG).commit();

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);

        fabAction();
        menuBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }


    private void menuBottomNav() {

        btmNav = findViewById(R.id.bottomNav);

        btmNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        manager.beginTransaction().hide(active).show(frHome).commit();
                        active = frHome;
                        fab.show();
                        return true;


                    case R.id.settings:
                        manager.beginTransaction().hide(active).show(frProfile).commit();
                        active = frProfile;
                        fab.hide();
                        return true;
                }
                return false;
            }
        });

    }


    private void fabAction() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }


}
