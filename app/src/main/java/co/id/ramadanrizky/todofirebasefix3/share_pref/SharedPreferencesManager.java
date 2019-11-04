package co.id.ramadanrizky.todofirebasefix3.share_pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {

    SharedPreferences sharedPreferences;

    static final String KEY_STATUS_LOGGED_IN = "status_logged_in";


   public SharedPreferencesManager(Context context){
        sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);
   }


    public void setKeyStatusLoggedIn(boolean statusLoggedIn){
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putBoolean(KEY_STATUS_LOGGED_IN, statusLoggedIn);
       editor.commit();
    }

    public boolean getKeyStatusLoggedIn() {
       Boolean statusLoggedIn = sharedPreferences.getBoolean(KEY_STATUS_LOGGED_IN, false);
       return statusLoggedIn;
    }

    public void clearLoggedInUser(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_STATUS_LOGGED_IN);
        editor.commit();
    }

    public void saveNightModeState(Boolean state){
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putBoolean("nightmode", state);
       editor.commit();
   }

   public Boolean loadNightModeState (){
       Boolean state = sharedPreferences.getBoolean("nightmode", false);
       return state;
   }

}
