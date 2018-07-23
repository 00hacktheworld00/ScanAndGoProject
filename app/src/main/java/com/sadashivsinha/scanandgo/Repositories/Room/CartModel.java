package com.sadashivsinha.scanandgo.Repositories.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by sadashivsinha on 21/07/18.
 */

@Entity(tableName = "cart_table")
public class CartModel {

    @PrimaryKey()
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "itemName")
    private String itemName;


    @NonNull
    @ColumnInfo(name = "itemQuantity")
    private int itemQuantity;



    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

//    private String itemName;
//    private int itemQuantity;
//    private long id;
//
//    public CartModel(){
//
//    }
//
//    public CartModel(long id, String itemName, int itemQuantity) {
//        this.itemName = itemName;
//        this.itemQuantity = itemQuantity;
//        this.id = id;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public String getItemName() {
//        return itemName;
//    }
//
//    public int getItemQuantity() {
//        return itemQuantity;
//    }
}
