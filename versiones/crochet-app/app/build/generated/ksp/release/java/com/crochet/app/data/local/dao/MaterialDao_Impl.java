package com.crochet.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.crochet.app.data.local.entity.MaterialEntity;
import com.crochet.app.data.local.entity.MaterialTagCrossRef;
import com.crochet.app.data.local.entity.TagEntity;
import com.crochet.app.data.local.relation.MaterialWithTags;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MaterialDao_Impl implements MaterialDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MaterialEntity> __insertionAdapterOfMaterialEntity;

  private final EntityInsertionAdapter<MaterialTagCrossRef> __insertionAdapterOfMaterialTagCrossRef;

  private final EntityDeletionOrUpdateAdapter<MaterialEntity> __updateAdapterOfMaterialEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMaterialById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCrossRefsForMaterial;

  public MaterialDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMaterialEntity = new EntityInsertionAdapter<MaterialEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `materials` (`id`,`name`,`quantity`,`unit`,`imagePath`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaterialEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getQuantity());
        statement.bindString(4, entity.getUnit());
        if (entity.getImagePath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getImagePath());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfMaterialTagCrossRef = new EntityInsertionAdapter<MaterialTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `material_tag_cross_ref` (`materialId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaterialTagCrossRef entity) {
        statement.bindLong(1, entity.getMaterialId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__updateAdapterOfMaterialEntity = new EntityDeletionOrUpdateAdapter<MaterialEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `materials` SET `id` = ?,`name` = ?,`quantity` = ?,`unit` = ?,`imagePath` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaterialEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getQuantity());
        statement.bindString(4, entity.getUnit());
        if (entity.getImagePath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getImagePath());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteMaterialById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM materials WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteCrossRefsForMaterial = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM material_tag_cross_ref WHERE materialId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMaterial(final MaterialEntity material,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMaterialEntity.insertAndReturnId(material);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCrossRefs(final List<MaterialTagCrossRef> crossRefs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMaterialTagCrossRef.insert(crossRefs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMaterial(final MaterialEntity material,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMaterialEntity.handle(material);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMaterialWithTags(final MaterialEntity material, final List<Long> tagIds,
      final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MaterialDao.DefaultImpls.insertMaterialWithTags(MaterialDao_Impl.this, material, tagIds, __cont), $completion);
  }

  @Override
  public Object updateMaterialWithTags(final MaterialEntity material, final List<Long> tagIds,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MaterialDao.DefaultImpls.updateMaterialWithTags(MaterialDao_Impl.this, material, tagIds, __cont), $completion);
  }

  @Override
  public Object deleteMaterialById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMaterialById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMaterialById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCrossRefsForMaterial(final long materialId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCrossRefsForMaterial.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, materialId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteCrossRefsForMaterial.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MaterialWithTags>> getMaterialsWithTags(final String searchQuery) {
    final String _sql = "\n"
            + "        SELECT * FROM materials\n"
            + "        WHERE (? = '' OR name LIKE '%' || ? || '%' OR notes LIKE '%' || ? || '%')\n"
            + "        ORDER BY createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, searchQuery);
    _argIndex = 2;
    _statement.bindString(_argIndex, searchQuery);
    _argIndex = 3;
    _statement.bindString(_argIndex, searchQuery);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"material_tag_cross_ref", "tags",
        "materials"}, new Callable<List<MaterialWithTags>>() {
      @Override
      @NonNull
      public List<MaterialWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
            final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
            final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<TagEntity>> _collectionTags = new LongSparseArray<ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(_collectionTags);
            final List<MaterialWithTags> _result = new ArrayList<MaterialWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final MaterialWithTags _item;
              final MaterialEntity _tmpMaterial;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final double _tmpQuantity;
              _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
              final String _tmpUnit;
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
              final String _tmpImagePath;
              if (_cursor.isNull(_cursorIndexOfImagePath)) {
                _tmpImagePath = null;
              } else {
                _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpMaterial = new MaterialEntity(_tmpId,_tmpName,_tmpQuantity,_tmpUnit,_tmpImagePath,_tmpNotes,_tmpCreatedAt);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _item = new MaterialWithTags(_tmpMaterial,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<MaterialWithTags> getMaterialWithTagsById(final long id) {
    final String _sql = "SELECT * FROM materials WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"material_tag_cross_ref", "tags",
        "materials"}, new Callable<MaterialWithTags>() {
      @Override
      @Nullable
      public MaterialWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
            final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
            final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<TagEntity>> _collectionTags = new LongSparseArray<ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(_collectionTags);
            final MaterialWithTags _result;
            if (_cursor.moveToFirst()) {
              final MaterialEntity _tmpMaterial;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final double _tmpQuantity;
              _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
              final String _tmpUnit;
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
              final String _tmpImagePath;
              if (_cursor.isNull(_cursorIndexOfImagePath)) {
                _tmpImagePath = null;
              } else {
                _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpMaterial = new MaterialEntity(_tmpId,_tmpName,_tmpQuantity,_tmpUnit,_tmpImagePath,_tmpNotes,_tmpCreatedAt);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _result = new MaterialWithTags(_tmpMaterial,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(
      @NonNull final LongSparseArray<ArrayList<TagEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `tags`.`id` AS `id`,`tags`.`name` AS `name`,`tags`.`colorHex` AS `colorHex`,_junction.`materialId` FROM `material_tag_cross_ref` AS _junction INNER JOIN `tags` ON (_junction.`tagId` = `tags`.`id`) WHERE _junction.`materialId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.materialId;
      final int _itemKeyIndex = 3;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfColorHex = 2;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<TagEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final TagEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpColorHex;
          _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
          _item_1 = new TagEntity(_tmpId,_tmpName,_tmpColorHex);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
