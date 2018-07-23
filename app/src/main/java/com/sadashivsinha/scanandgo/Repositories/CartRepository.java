package com.sadashivsinha.scanandgo.Repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.sadashivsinha.scanandgo.Repositories.Room.CartDao;
import com.sadashivsinha.scanandgo.Repositories.Room.CartModel;
import com.sadashivsinha.scanandgo.Repositories.Room.CartRoomDatabase;

import java.util.List;

/**
 * Created by sadashivsinha on 23/07/18.
 */

public class CartRepository {


    private CartDao mCartDao;
    private LiveData<List<CartModel>> mAllCartItems;

    public CartRepository(Application application) {


        CartRoomDatabase db = CartRoomDatabase.getDatabase(application);
        mCartDao = db.cartDao();
        mAllCartItems = mCartDao.getCartItems();

    }


    public LiveData<List<CartModel>> getAllCartItems() {
        return mAllCartItems;
    }


    public void insert(CartModel cartModel) {
        new insertAsyncTask(mCartDao).execute(cartModel);
    }

    public void update(CartModel cartModel) {
        new updateAsyncTask(mCartDao).execute(cartModel);
    }

    public void removeAllItems() {
        new removeAllItemsAsyncTask(mCartDao).execute();
    }
    public void removeItem(CartModel cartModel) {
        new removeItemAsyncTask(mCartDao).execute(cartModel);
    }


    private static class insertAsyncTask extends AsyncTask<CartModel, Void, Void> {

        private CartDao mAsyncTaskDao;

        insertAsyncTask(CartDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CartModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class removeAllItemsAsyncTask extends AsyncTask<CartModel, Void, Void> {

        private CartDao mAsyncTaskDao;

        removeAllItemsAsyncTask(CartDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CartModel... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }


    private static class removeItemAsyncTask extends AsyncTask<CartModel, Void, Void> {

        private CartDao mAsyncTaskDao;

        removeItemAsyncTask(CartDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CartModel... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<CartModel, Void, Void> {

        private CartDao mAsyncTaskDao;

        updateAsyncTask(CartDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CartModel... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }


}
