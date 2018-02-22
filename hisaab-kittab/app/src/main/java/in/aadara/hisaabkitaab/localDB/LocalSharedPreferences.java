package in.aadara.hisaabkitaab.localDB;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by umashankarpathak on 15/01/18.
 */

public class LocalSharedPreferences {
    private static LocalSharedPreferences localSharedPreferences;
    public static LocalSharedPreferences getLocalSharedPreferences(){
        if(localSharedPreferences == null){
            localSharedPreferences = new LocalSharedPreferences();
        }
        return localSharedPreferences;
    }

    public void save(Context context,String key,String value){
        SharedPreferences settings = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void saveBool(Context context,String key,boolean value){
        SharedPreferences settings = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public boolean get(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        return settings.getBoolean(key,false);
    }

    public String getValue(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        return settings.getString(key,"");
    }
}
