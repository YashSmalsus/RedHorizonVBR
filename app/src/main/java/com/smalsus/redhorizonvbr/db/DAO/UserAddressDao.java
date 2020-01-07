package com.smalsus.redhorizonvbr.db.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;

import java.util.List;

@Dao
public interface UserAddressDao {
    @Insert
    void insert(UserDeliveryAddress userDeliveryAddress);

    @Query("DELETE FROM UserDeliveryAddress where id =:id")
    void deleteAddressItem(int id);

    @Query("SELECT * from UserDeliveryAddress ")
    List<UserDeliveryAddress> getAllWords();

    @Update
    int updateUserDeliveryAddress(UserDeliveryAddress tour);
}
