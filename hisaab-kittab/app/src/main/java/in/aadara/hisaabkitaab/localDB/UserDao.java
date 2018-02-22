package in.aadara.hisaabkitaab.localDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by umashankarpathak on 15/01/18.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Insert
    void insertAll(User... users);

    @Update
    void updateUsers(User... users);

    @Delete
    void delete(User user);

    @Delete
    void deleteAll(User... user);
}
