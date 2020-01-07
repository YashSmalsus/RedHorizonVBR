package com.smalsus.redhorizonvbr.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.smalsus.redhorizonvbr.db.DAO.UserAddressDao;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;

@Database(entities = UserDeliveryAddress.class, version = 1,exportSchema = false)
abstract public class UserAddressDatabase extends RoomDatabase {

    private static final String DB_NAME = "user_address_database";
    private static volatile UserAddressDatabase INSTANCE;

    public static UserAddressDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserAddressDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserAddressDatabase.class, DB_NAME).fallbackToDestructiveMigration()
                            .build();
                }

            }
        }
        return INSTANCE;
    }

    public abstract UserAddressDao userAddressDatabase();
}
