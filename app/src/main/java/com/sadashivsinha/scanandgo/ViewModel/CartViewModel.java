package com.sadashivsinha.scanandgo.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sadashivsinha.scanandgo.Repositories.CartRepository;
import com.sadashivsinha.scanandgo.Repositories.Room.CartModel;

import java.util.List;

/**
 * Created by sadashivsinha on 23/07/18.
 */

public class CartViewModel extends AndroidViewModel {

    CartRepository mCartRepository;
    LiveData<List<CartModel>> mAllCartItems;

    public CartViewModel(@NonNull Application application) {
        super(application);

        mCartRepository = new CartRepository(application);
        mAllCartItems = mCartRepository.getAllCartItems();
    }


    public LiveData<List<CartModel>> getAllCartItems() {
        return mAllCartItems;
    }


    public void insert(CartModel cartModel) {
        mCartRepository.insert(cartModel);
    }


    public void removeAllItems() {
        mCartRepository.removeAllItems();
    }

    public void updateItem(CartModel cartModel) {
        mCartRepository.update(cartModel);
    }

    public void removeItem(CartModel cartModel) {
        mCartRepository.removeItem(cartModel);
    }
}