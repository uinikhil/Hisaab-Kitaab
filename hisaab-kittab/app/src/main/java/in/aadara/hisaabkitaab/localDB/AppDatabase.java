package in.aadara.hisaabkitaab.localDB;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by umashankarpathak on 15/01/18.
 */

public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;

    public static AppDatabase AppDatabaseSingleton(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context,
                    AppDatabase.class, "user-db").build();
        }
        return appDatabase;
    }

    public abstract UserDao userDao();
}
