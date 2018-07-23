package com.sadashivsinha.scanandgo.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;

import com.sadashivsinha.scanandgo.Models.CartModelDAO_Impl;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class AppDatabase_Impl extends AppDatabase {
  private volatile CartModelDAO _cartModelDAO;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `cart_table` (`primaryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` INTEGER NOT NULL, `itemName` TEXT, `itemQuantity` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"347682bdaf281e4f1078a38538f2900c\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `cart_table`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsCartTable = new HashMap<String, TableInfo.Column>(4);
        _columnsCartTable.put("primaryId", new TableInfo.Column("primaryId", "INTEGER", true, 1));
        _columnsCartTable.put("id", new TableInfo.Column("id", "INTEGER", true, 0));
        _columnsCartTable.put("itemName", new TableInfo.Column("itemName", "TEXT", false, 0));
        _columnsCartTable.put("itemQuantity", new TableInfo.Column("itemQuantity", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCartTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCartTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCartTable = new TableInfo("cart_table", _columnsCartTable, _foreignKeysCartTable, _indicesCartTable);
        final TableInfo _existingCartTable = TableInfo.read(_db, "cart_table");
        if (! _infoCartTable.equals(_existingCartTable)) {
          throw new IllegalStateException("Migration didn't properly handle cart_table(com.sadashivsinha.scanandgo.Repositories.Room.CartModel).\n"
                  + " Expected:\n" + _infoCartTable + "\n"
                  + " Found:\n" + _existingCartTable);
        }
      }
    }, "347682bdaf281e4f1078a38538f2900c", "053e841b00ae604917102a9145383eb8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "cart_table");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cart_table`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public CartModelDAO cartModelDAO() {
    if (_cartModelDAO != null) {
      return _cartModelDAO;
    } else {
      synchronized(this) {
        if(_cartModelDAO == null) {
          _cartModelDAO = new CartModelDAO_Impl(this);
        }
        return _cartModelDAO;
      }
    }
  }
}
