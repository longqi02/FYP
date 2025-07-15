package com.example.trafficsignapp.data.detection;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DetectedSignDao_Impl implements DetectedSignDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DetectedSignEntity> __insertionAdapterOfDetectedSignEntity;

  public DetectedSignDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDetectedSignEntity = new EntityInsertionAdapter<DetectedSignEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `detected_signs` (`detectionId`,`classId`,`confidence`,`timestamp`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DetectedSignEntity value) {
        stmt.bindLong(1, value.getDetectionId());
        stmt.bindLong(2, value.getClassId());
        stmt.bindDouble(3, value.getConfidence());
        stmt.bindLong(4, value.getTimestamp());
      }
    };
  }

  @Override
  public long insert(final DetectedSignEntity sign) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfDetectedSignEntity.insertAndReturnId(sign);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<DetectedSignEntity> getAll() {
    final String _sql = "SELECT * FROM detected_signs ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDetectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "detectionId");
      final int _cursorIndexOfClassId = CursorUtil.getColumnIndexOrThrow(_cursor, "classId");
      final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<DetectedSignEntity> _result = new ArrayList<DetectedSignEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DetectedSignEntity _item;
        final long _tmpDetectionId;
        _tmpDetectionId = _cursor.getLong(_cursorIndexOfDetectionId);
        final int _tmpClassId;
        _tmpClassId = _cursor.getInt(_cursorIndexOfClassId);
        final float _tmpConfidence;
        _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item = new DetectedSignEntity(_tmpDetectionId,_tmpClassId,_tmpConfidence,_tmpTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DetectedSignEntity> getAllDetections() {
    final String _sql = "SELECT * FROM detected_signs ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDetectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "detectionId");
      final int _cursorIndexOfClassId = CursorUtil.getColumnIndexOrThrow(_cursor, "classId");
      final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<DetectedSignEntity> _result = new ArrayList<DetectedSignEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DetectedSignEntity _item;
        final long _tmpDetectionId;
        _tmpDetectionId = _cursor.getLong(_cursorIndexOfDetectionId);
        final int _tmpClassId;
        _tmpClassId = _cursor.getInt(_cursorIndexOfClassId);
        final float _tmpConfidence;
        _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item = new DetectedSignEntity(_tmpDetectionId,_tmpClassId,_tmpConfidence,_tmpTimestamp);
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
