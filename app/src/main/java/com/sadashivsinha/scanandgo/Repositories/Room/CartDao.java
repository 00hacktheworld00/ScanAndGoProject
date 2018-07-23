package com.sadashivsinha.scanandgo.Repositories.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by sadashivsinha on 23/07/18.
 */


@Dao
public interface CartDao {

    @Query("SELECT * FROM cart_table order by id asc")
    LiveData<List<CartModel>> getCartItems();

    @Insert
    void insert(CartModel cartModel);

    @Delete
    void delete(CartModel cartModel);

    @Update
    void update(CartModel cartModel);

    @Query("Delete from cart_table")
    void deleteAll();
}