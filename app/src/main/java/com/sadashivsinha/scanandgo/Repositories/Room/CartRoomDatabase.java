package com.sadashivsinha.scanandgo.Repositories.Room;

/**
 * Created by sadashivsinha on 23/07/18.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {CartModel.class}, version = 1 )
public abstract class CartRoomDatabase extends RoomDatabase{

    public abstract CartDao cartDao();


    private static CartRoomDatabase INSTANCE;

    public static CartRoomDatabase getDatabase(final Context context)
    {

        if(INSTANCE == null)
        {

            synchronized (CartRoomDatabase.class)
            {
                if(INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CartRoomDatabase.class, "cart_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }}


        return INSTANCE;

    }



}