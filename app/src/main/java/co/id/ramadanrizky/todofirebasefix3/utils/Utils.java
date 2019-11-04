package co.id.ramadanrizky.todofirebasefix3.utils;

import android.app.Activity;
import android.content.Intent;

import co.id.ramadanrizky.todofirebasefix3.R;

public class Utils {

    private static int sTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    public static void changeToTheme(Activity activity, int theme){
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
//        activity.recreate();
    }

    public static void onActivityCreateSetTheme(Activity activity){
        switch (sTheme){
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.ActivityTheme_Light);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.ActivityTheme_Dark);
                break;
        }
    }

}
