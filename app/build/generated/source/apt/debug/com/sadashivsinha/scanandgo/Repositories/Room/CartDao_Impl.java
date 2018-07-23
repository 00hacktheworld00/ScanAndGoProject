package com.sadashivsinha.scanandgo.Repositories.Room;

import android.arch.lifecycle.ComputableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.InvalidationTracker.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import android.support.annotation.NonNull;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class CartDao_Impl implements CartDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfCartModel;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfCartModel;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfCartModel;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CartDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCartModel = new EntityInsertionAdapter<CartModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `cart_table`(`id`,`itemName`,`itemQuantity`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getItemName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getItemName());
        }
        stmt.bindLong(3, value.getItemQuantity());
      }
    };
    this.__deletionAdapterOfCartModel = new EntityDeletionOrUpdateAdapter<CartModel>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `cart_table` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartModel value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfCartModel = new EntityDeletionOrUpdateAdapter<CartModel>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `cart_table` SET `id` = ?,`itemName` = ?,`itemQuantity` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CartModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getItemName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getItemName());
        }
        stmt.bindLong(3, value.getItemQuantity());
        stmt.bindLong(4, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "Delete from cart_table";
        return _query;
      }
    };
  }

  @Override
  public void insert(CartModel cartModel) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfCartModel.insert(cartModel);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(CartModel cartModel) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfCartModel.handle(cartModel);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(CartModel cartModel) {
    __db.beginTransaction();
    try {
      __updateAdapterOfCartModel.handle(cartModel);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<CartModel>> getCartItems() {
    final String _sql = "SELECT * FROM cart_table order by id asc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<CartModel>>() {
      private Observer _observer;

      @Override
      protected List<CartModel> compute() {
        if (_observer == null) {
          _observer = new Observer("cart_table") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfItemName = _cursor.getColumnIndexOrThrow("itemName");
          final int _cursorIndexOfItemQuantity = _cursor.getColumnIndexOrThrow("itemQuantity");
          final List<CartModel> _result = new ArrayList<CartModel>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final CartModel _item;
            _item = new CartModel();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpItemName;
            _tmpItemName = _cursor.getString(_cursorIndexOfItemName);
            _item.setItemName(_tmpItemName);
            final int _tmpItemQuantity;
            _tmpItemQuantity = _cursor.getInt(_cursorIndexOfItemQuantity);
            _item.setItemQuantity(_tmpItemQuantity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }
}
