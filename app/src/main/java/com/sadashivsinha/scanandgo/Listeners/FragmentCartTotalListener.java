package com.sadashivsinha.scanandgo.Listeners;

/**
 * Created by sadashivsinha on 21/07/18.
 */

public interface FragmentCartTotalListener {
    void onValuesChanged(long id, String itemName, int quantity, Boolean addingQuantity);
    void onValuesDeleted(long id, String itemName, int quantity);
}
