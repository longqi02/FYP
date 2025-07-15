package com.example.trafficsignapp.data.route;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RouteDao_Impl implements RouteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Route> __insertionAdapterOfRoute;

  private final EntityInsertionAdapter<RouteDetectionCrossRef> __insertionAdapterOfRouteDetectionCrossRef;

  private final SharedSQLiteStatement __preparedStmtOfUpdateEndTime;

  public RouteDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoute = new EntityInsertionAdapter<Route>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `routes` (`routeId`,`routeKey`,`userId`,`startTime`,`endTime`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Route value) {
        stmt.bindLong(1, value.getRouteId());
        if (value.getRouteKey() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getRouteKey());
        }
        if (value.getUserId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getUserId());
        }
        stmt.bindLong(4, value.getStartTime());
        if (value.getEndTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, value.getEndTime());
        }
      }
    };
    this.__insertionAdapterOfRouteDetectionCrossRef = new EntityInsertionAdapter<RouteDetectionCrossRef>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `route_detection_ref` (`routeId`,`detectionId`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RouteDetectionCrossRef value) {
        stmt.bindLong(1, value.getRouteId());
        stmt.bindLong(2, value.getDetectionId());
      }
    };
    this.__preparedStmtOfUpdateEndTime = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE routes SET endTime = ? WHERE routeId = ?";
        return _query;
      }
    };
  }

  @Override
  public long createRoute(final Route route) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfRoute.insertAndReturnId(route);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void linkDetection(final RouteDetectionCrossRef ref) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfRouteDetectionCrossRef.insert(ref);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int updateEndTime(final long routeId, final long endTime) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateEndTime.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, endTime);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, routeId);
    __db.beginTransaction();
    try {
      final int _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateEndTime.release(_stmt);
    }
  }

  @Override
  public RouteWithDetections getRouteWithDetections(final long id) {
    final String _sql = "SELECT * FROM routes WHERE routeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
      try {
        final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
        final int _cursorIndexOfRouteKey = CursorUtil.getColumnIndexOrThrow(_cursor, "routeKey");
        final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
        final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
        final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
        final RouteWithDetections _result;
        if(_cursor.moveToFirst()) {
          final Route _tmpRoute;
          final long _tmpRouteId;
          _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
          final String _tmpRouteKey;
          if (_cursor.isNull(_cursorIndexOfRouteKey)) {
            _tmpRouteKey = null;
          } else {
            _tmpRouteKey = _cursor.getString(_cursorIndexOfRouteKey);
          }
          final String _tmpUserId;
          if (_cursor.isNull(_cursorIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
          }
          final long _tmpStartTime;
          _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
          final Long _tmpEndTime;
          if (_cursor.isNull(_cursorIndexOfEndTime)) {
            _tmpEndTime = null;
          } else {
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
          }
          _tmpRoute = new Route(_tmpRouteId,_tmpRouteKey,_tmpUserId,_tmpStartTime,_tmpEndTime);
          _result = new RouteWithDetections(_tmpRoute,null);
        } else {
          _result = null;
        }
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        _cursor.close();
        _statement.release();
      }
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Route findByKey(final String routeKey) {
    final String _sql = "SELECT * FROM routes WHERE routeKey = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (routeKey == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, routeKey);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
      final int _cursorIndexOfRouteKey = CursorUtil.getColumnIndexOrThrow(_cursor, "routeKey");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
      final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
      final Route _result;
      if(_cursor.moveToFirst()) {
        final long _tmpRouteId;
        _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
        final String _tmpRouteKey;
        if (_cursor.isNull(_cursorIndexOfRouteKey)) {
          _tmpRouteKey = null;
        } else {
          _tmpRouteKey = _cursor.getString(_cursorIndexOfRouteKey);
        }
        final String _tmpUserId;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmpUserId = null;
        } else {
          _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        }
        final long _tmpStartTime;
        _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
        final Long _tmpEndTime;
        if (_cursor.isNull(_cursorIndexOfEndTime)) {
          _tmpEndTime = null;
        } else {
          _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
        }
        _result = new Route(_tmpRouteId,_tmpRouteKey,_tmpUserId,_tmpStartTime,_tmpEndTime);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Route> getAllRoutesForUser(final String userId) {
    final String _sql = "SELECT * FROM routes WHERE userId = ? ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
      final int _cursorIndexOfRouteKey = CursorUtil.getColumnIndexOrThrow(_cursor, "routeKey");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
      final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
      final List<Route> _result = new ArrayList<Route>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Route _item;
        final long _tmpRouteId;
        _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
        final String _tmpRouteKey;
        if (_cursor.isNull(_cursorIndexOfRouteKey)) {
          _tmpRouteKey = null;
        } else {
          _tmpRouteKey = _cursor.getString(_cursorIndexOfRouteKey);
        }
        final String _tmpUserId;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmpUserId = null;
        } else {
          _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        }
        final long _tmpStartTime;
        _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
        final Long _tmpEndTime;
        if (_cursor.isNull(_cursorIndexOfEndTime)) {
          _tmpEndTime = null;
        } else {
          _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
        }
        _item = new Route(_tmpRouteId,_tmpRouteKey,_tmpUserId,_tmpStartTime,_tmpEndTime);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
