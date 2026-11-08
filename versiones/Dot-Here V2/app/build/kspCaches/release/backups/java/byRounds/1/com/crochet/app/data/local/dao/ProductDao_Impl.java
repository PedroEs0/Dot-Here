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
import com.crochet.app.data.local.entity.ProductEntity;
import com.crochet.app.data.local.entity.ProductMaterialCrossRef;
import com.crochet.app.data.local.entity.ProductTagCrossRef;
import com.crochet.app.data.local.entity.TagEntity;
import com.crochet.app.data.local.relation.ProductWithDetails;
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
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  private final EntityInsertionAdapter<ProductMaterialCrossRef> __insertionAdapterOfProductMaterialCrossRef;

  private final EntityInsertionAdapter<ProductTagCrossRef> __insertionAdapterOfProductTagCrossRef;

  private final EntityDeletionOrUpdateAdapter<ProductEntity> __updateAdapterOfProductEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteProductById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMaterialCrossRefsForProduct;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTagCrossRefsForProduct;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `products` (`id`,`name`,`description`,`price`,`dimensions`,`frontImagePath`,`backImagePath`,`leftImagePath`,`rightImagePath`,`topImagePath`,`bottomImagePath`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindDouble(4, entity.getPrice());
        if (entity.getDimensions() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDimensions());
        }
        if (entity.getFrontImagePath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFrontImagePath());
        }
        if (entity.getBackImagePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getBackImagePath());
        }
        if (entity.getLeftImagePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLeftImagePath());
        }
        if (entity.getRightImagePath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRightImagePath());
        }
        if (entity.getTopImagePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTopImagePath());
        }
        if (entity.getBottomImagePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getBottomImagePath());
        }
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfProductMaterialCrossRef = new EntityInsertionAdapter<ProductMaterialCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `product_material_cross_ref` (`productId`,`materialId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductMaterialCrossRef entity) {
        statement.bindLong(1, entity.getProductId());
        statement.bindLong(2, entity.getMaterialId());
      }
    };
    this.__insertionAdapterOfProductTagCrossRef = new EntityInsertionAdapter<ProductTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `product_tag_cross_ref` (`productId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductTagCrossRef entity) {
        statement.bindLong(1, entity.getProductId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__updateAdapterOfProductEntity = new EntityDeletionOrUpdateAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `products` SET `id` = ?,`name` = ?,`description` = ?,`price` = ?,`dimensions` = ?,`frontImagePath` = ?,`backImagePath` = ?,`leftImagePath` = ?,`rightImagePath` = ?,`topImagePath` = ?,`bottomImagePath` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindDouble(4, entity.getPrice());
        if (entity.getDimensions() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDimensions());
        }
        if (entity.getFrontImagePath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFrontImagePath());
        }
        if (entity.getBackImagePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getBackImagePath());
        }
        if (entity.getLeftImagePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLeftImagePath());
        }
        if (entity.getRightImagePath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRightImagePath());
        }
        if (entity.getTopImagePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTopImagePath());
        }
        if (entity.getBottomImagePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getBottomImagePath());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteProductById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM products WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMaterialCrossRefsForProduct = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM product_material_cross_ref WHERE productId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTagCrossRefsForProduct = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM product_tag_cross_ref WHERE productId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertProduct(final ProductEntity product,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfProductEntity.insertAndReturnId(product);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMaterialCrossRefs(final List<ProductMaterialCrossRef> crossRefs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductMaterialCrossRef.insert(crossRefs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTagCrossRefs(final List<ProductTagCrossRef> crossRefs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductTagCrossRef.insert(crossRefs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProduct(final ProductEntity product,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProductEntity.handle(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertProductWithDetails(final ProductEntity product, final List<Long> materialIds,
      final List<Long> tagIds, final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ProductDao.DefaultImpls.insertProductWithDetails(ProductDao_Impl.this, product, materialIds, tagIds, __cont), $completion);
  }

  @Override
  public Object updateProductWithDetails(final ProductEntity product, final List<Long> materialIds,
      final List<Long> tagIds, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ProductDao.DefaultImpls.updateProductWithDetails(ProductDao_Impl.this, product, materialIds, tagIds, __cont), $completion);
  }

  @Override
  public Object deleteProductById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteProductById.acquire();
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
          __preparedStmtOfDeleteProductById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMaterialCrossRefsForProduct(final long productId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMaterialCrossRefsForProduct.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, productId);
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
          __preparedStmtOfDeleteMaterialCrossRefsForProduct.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTagCrossRefsForProduct(final long productId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTagCrossRefsForProduct.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, productId);
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
          __preparedStmtOfDeleteTagCrossRefsForProduct.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProductWithDetails>> getProductsWithDetails(final String searchQuery) {
    final String _sql = "\n"
            + "        SELECT * FROM products\n"
            + "        WHERE (? = '' OR name LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%')\n"
            + "        ORDER BY createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, searchQuery);
    _argIndex = 2;
    _statement.bindString(_argIndex, searchQuery);
    _argIndex = 3;
    _statement.bindString(_argIndex, searchQuery);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"product_material_cross_ref",
        "materials", "product_tag_cross_ref", "tags",
        "products"}, new Callable<List<ProductWithDetails>>() {
      @Override
      @NonNull
      public List<ProductWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
            final int _cursorIndexOfDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "dimensions");
            final int _cursorIndexOfFrontImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImagePath");
            final int _cursorIndexOfBackImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "backImagePath");
            final int _cursorIndexOfLeftImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "leftImagePath");
            final int _cursorIndexOfRightImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "rightImagePath");
            final int _cursorIndexOfTopImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "topImagePath");
            final int _cursorIndexOfBottomImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "bottomImagePath");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<MaterialEntity>> _collectionMaterials = new LongSparseArray<ArrayList<MaterialEntity>>();
            final LongSparseArray<ArrayList<TagEntity>> _collectionTags = new LongSparseArray<ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionMaterials.containsKey(_tmpKey)) {
                _collectionMaterials.put(_tmpKey, new ArrayList<MaterialEntity>());
              }
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipmaterialsAscomCrochetAppDataLocalEntityMaterialEntity(_collectionMaterials);
            __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(_collectionTags);
            final List<ProductWithDetails> _result = new ArrayList<ProductWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ProductWithDetails _item;
              final ProductEntity _tmpProduct;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpDescription;
              if (_cursor.isNull(_cursorIndexOfDescription)) {
                _tmpDescription = null;
              } else {
                _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
              }
              final double _tmpPrice;
              _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
              final String _tmpDimensions;
              if (_cursor.isNull(_cursorIndexOfDimensions)) {
                _tmpDimensions = null;
              } else {
                _tmpDimensions = _cursor.getString(_cursorIndexOfDimensions);
              }
              final String _tmpFrontImagePath;
              if (_cursor.isNull(_cursorIndexOfFrontImagePath)) {
                _tmpFrontImagePath = null;
              } else {
                _tmpFrontImagePath = _cursor.getString(_cursorIndexOfFrontImagePath);
              }
              final String _tmpBackImagePath;
              if (_cursor.isNull(_cursorIndexOfBackImagePath)) {
                _tmpBackImagePath = null;
              } else {
                _tmpBackImagePath = _cursor.getString(_cursorIndexOfBackImagePath);
              }
              final String _tmpLeftImagePath;
              if (_cursor.isNull(_cursorIndexOfLeftImagePath)) {
                _tmpLeftImagePath = null;
              } else {
                _tmpLeftImagePath = _cursor.getString(_cursorIndexOfLeftImagePath);
              }
              final String _tmpRightImagePath;
              if (_cursor.isNull(_cursorIndexOfRightImagePath)) {
                _tmpRightImagePath = null;
              } else {
                _tmpRightImagePath = _cursor.getString(_cursorIndexOfRightImagePath);
              }
              final String _tmpTopImagePath;
              if (_cursor.isNull(_cursorIndexOfTopImagePath)) {
                _tmpTopImagePath = null;
              } else {
                _tmpTopImagePath = _cursor.getString(_cursorIndexOfTopImagePath);
              }
              final String _tmpBottomImagePath;
              if (_cursor.isNull(_cursorIndexOfBottomImagePath)) {
                _tmpBottomImagePath = null;
              } else {
                _tmpBottomImagePath = _cursor.getString(_cursorIndexOfBottomImagePath);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpProduct = new ProductEntity(_tmpId,_tmpName,_tmpDescription,_tmpPrice,_tmpDimensions,_tmpFrontImagePath,_tmpBackImagePath,_tmpLeftImagePath,_tmpRightImagePath,_tmpTopImagePath,_tmpBottomImagePath,_tmpCreatedAt);
              final ArrayList<MaterialEntity> _tmpMaterialsCollection;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId);
              _tmpMaterialsCollection = _collectionMaterials.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new ProductWithDetails(_tmpProduct,_tmpMaterialsCollection,_tmpTagsCollection);
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
  public Flow<ProductWithDetails> getProductWithDetailsById(final long id) {
    final String _sql = "SELECT * FROM products WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"product_material_cross_ref",
        "materials", "product_tag_cross_ref", "tags",
        "products"}, new Callable<ProductWithDetails>() {
      @Override
      @Nullable
      public ProductWithDetails call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
            final int _cursorIndexOfDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "dimensions");
            final int _cursorIndexOfFrontImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImagePath");
            final int _cursorIndexOfBackImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "backImagePath");
            final int _cursorIndexOfLeftImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "leftImagePath");
            final int _cursorIndexOfRightImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "rightImagePath");
            final int _cursorIndexOfTopImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "topImagePath");
            final int _cursorIndexOfBottomImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "bottomImagePath");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<MaterialEntity>> _collectionMaterials = new LongSparseArray<ArrayList<MaterialEntity>>();
            final LongSparseArray<ArrayList<TagEntity>> _collectionTags = new LongSparseArray<ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionMaterials.containsKey(_tmpKey)) {
                _collectionMaterials.put(_tmpKey, new ArrayList<MaterialEntity>());
              }
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipmaterialsAscomCrochetAppDataLocalEntityMaterialEntity(_collectionMaterials);
            __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(_collectionTags);
            final ProductWithDetails _result;
            if (_cursor.moveToFirst()) {
              final ProductEntity _tmpProduct;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpDescription;
              if (_cursor.isNull(_cursorIndexOfDescription)) {
                _tmpDescription = null;
              } else {
                _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
              }
              final double _tmpPrice;
              _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
              final String _tmpDimensions;
              if (_cursor.isNull(_cursorIndexOfDimensions)) {
                _tmpDimensions = null;
              } else {
                _tmpDimensions = _cursor.getString(_cursorIndexOfDimensions);
              }
              final String _tmpFrontImagePath;
              if (_cursor.isNull(_cursorIndexOfFrontImagePath)) {
                _tmpFrontImagePath = null;
              } else {
                _tmpFrontImagePath = _cursor.getString(_cursorIndexOfFrontImagePath);
              }
              final String _tmpBackImagePath;
              if (_cursor.isNull(_cursorIndexOfBackImagePath)) {
                _tmpBackImagePath = null;
              } else {
                _tmpBackImagePath = _cursor.getString(_cursorIndexOfBackImagePath);
              }
              final String _tmpLeftImagePath;
              if (_cursor.isNull(_cursorIndexOfLeftImagePath)) {
                _tmpLeftImagePath = null;
              } else {
                _tmpLeftImagePath = _cursor.getString(_cursorIndexOfLeftImagePath);
              }
              final String _tmpRightImagePath;
              if (_cursor.isNull(_cursorIndexOfRightImagePath)) {
                _tmpRightImagePath = null;
              } else {
                _tmpRightImagePath = _cursor.getString(_cursorIndexOfRightImagePath);
              }
              final String _tmpTopImagePath;
              if (_cursor.isNull(_cursorIndexOfTopImagePath)) {
                _tmpTopImagePath = null;
              } else {
                _tmpTopImagePath = _cursor.getString(_cursorIndexOfTopImagePath);
              }
              final String _tmpBottomImagePath;
              if (_cursor.isNull(_cursorIndexOfBottomImagePath)) {
                _tmpBottomImagePath = null;
              } else {
                _tmpBottomImagePath = _cursor.getString(_cursorIndexOfBottomImagePath);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpProduct = new ProductEntity(_tmpId,_tmpName,_tmpDescription,_tmpPrice,_tmpDimensions,_tmpFrontImagePath,_tmpBackImagePath,_tmpLeftImagePath,_tmpRightImagePath,_tmpTopImagePath,_tmpBottomImagePath,_tmpCreatedAt);
              final ArrayList<MaterialEntity> _tmpMaterialsCollection;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId);
              _tmpMaterialsCollection = _collectionMaterials.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _result = new ProductWithDetails(_tmpProduct,_tmpMaterialsCollection,_tmpTagsCollection);
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

  @Override
  public Flow<List<ProductWithDetails>> getProductsForMaterial(final long materialId) {
    final String _sql = "\n"
            + "        SELECT p.* FROM products p\n"
            + "        INNER JOIN product_material_cross_ref pm ON p.id = pm.productId\n"
            + "        WHERE pm.materialId = ?\n"
            + "        ORDER BY p.createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, materialId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"product_material_cross_ref",
        "materials", "product_tag_cross_ref", "tags",
        "products"}, new Callable<List<ProductWithDetails>>() {
      @Override
      @NonNull
      public List<ProductWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
            final int _cursorIndexOfDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "dimensions");
            final int _cursorIndexOfFrontImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "frontImagePath");
            final int _cursorIndexOfBackImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "backImagePath");
            final int _cursorIndexOfLeftImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "leftImagePath");
            final int _cursorIndexOfRightImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "rightImagePath");
            final int _cursorIndexOfTopImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "topImagePath");
            final int _cursorIndexOfBottomImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "bottomImagePath");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<MaterialEntity>> _collectionMaterials = new LongSparseArray<ArrayList<MaterialEntity>>();
            final LongSparseArray<ArrayList<TagEntity>> _collectionTags = new LongSparseArray<ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionMaterials.containsKey(_tmpKey)) {
                _collectionMaterials.put(_tmpKey, new ArrayList<MaterialEntity>());
              }
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipmaterialsAscomCrochetAppDataLocalEntityMaterialEntity(_collectionMaterials);
            __fetchRelationshiptagsAscomCrochetAppDataLocalEntityTagEntity(_collectionTags);
            final List<ProductWithDetails> _result = new ArrayList<ProductWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ProductWithDetails _item;
              final ProductEntity _tmpProduct;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpDescription;
              if (_cursor.isNull(_cursorIndexOfDescription)) {
                _tmpDescription = null;
              } else {
                _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
              }
              final double _tmpPrice;
              _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
              final String _tmpDimensions;
              if (_cursor.isNull(_cursorIndexOfDimensions)) {
                _tmpDimensions = null;
              } else {
                _tmpDimensions = _cursor.getString(_cursorIndexOfDimensions);
              }
              final String _tmpFrontImagePath;
              if (_cursor.isNull(_cursorIndexOfFrontImagePath)) {
                _tmpFrontImagePath = null;
              } else {
                _tmpFrontImagePath = _cursor.getString(_cursorIndexOfFrontImagePath);
              }
              final String _tmpBackImagePath;
              if (_cursor.isNull(_cursorIndexOfBackImagePath)) {
                _tmpBackImagePath = null;
              } else {
                _tmpBackImagePath = _cursor.getString(_cursorIndexOfBackImagePath);
              }
              final String _tmpLeftImagePath;
              if (_cursor.isNull(_cursorIndexOfLeftImagePath)) {
                _tmpLeftImagePath = null;
              } else {
                _tmpLeftImagePath = _cursor.getString(_cursorIndexOfLeftImagePath);
              }
              final String _tmpRightImagePath;
              if (_cursor.isNull(_cursorIndexOfRightImagePath)) {
                _tmpRightImagePath = null;
              } else {
                _tmpRightImagePath = _cursor.getString(_cursorIndexOfRightImagePath);
              }
              final String _tmpTopImagePath;
              if (_cursor.isNull(_cursorIndexOfTopImagePath)) {
                _tmpTopImagePath = null;
              } else {
                _tmpTopImagePath = _cursor.getString(_cursorIndexOfTopImagePath);
              }
              final String _tmpBottomImagePath;
              if (_cursor.isNull(_cursorIndexOfBottomImagePath)) {
                _tmpBottomImagePath = null;
              } else {
                _tmpBottomImagePath = _cursor.getString(_cursorIndexOfBottomImagePath);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpProduct = new ProductEntity(_tmpId,_tmpName,_tmpDescription,_tmpPrice,_tmpDimensions,_tmpFrontImagePath,_tmpBackImagePath,_tmpLeftImagePath,_tmpRightImagePath,_tmpTopImagePath,_tmpBottomImagePath,_tmpCreatedAt);
              final ArrayList<MaterialEntity> _tmpMaterialsCollection;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId);
              _tmpMaterialsCollection = _collectionMaterials.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new ProductWithDetails(_tmpProduct,_tmpMaterialsCollection,_tmpTagsCollection);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipmaterialsAscomCrochetAppDataLocalEntityMaterialEntity(
      @NonNull final LongSparseArray<ArrayList<MaterialEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipmaterialsAscomCrochetAppDataLocalEntityMaterialEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `materials`.`id` AS `id`,`materials`.`name` AS `name`,`materials`.`quantity` AS `quantity`,`materials`.`unit` AS `unit`,`materials`.`imagePath` AS `imagePath`,`materials`.`notes` AS `notes`,`materials`.`createdAt` AS `createdAt`,_junction.`productId` FROM `product_material_cross_ref` AS _junction INNER JOIN `materials` ON (_junction.`materialId` = `materials`.`id`) WHERE _junction.`productId` IN (");
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
      // _junction.productId;
      final int _itemKeyIndex = 7;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfQuantity = 2;
      final int _cursorIndexOfUnit = 3;
      final int _cursorIndexOfImagePath = 4;
      final int _cursorIndexOfNotes = 5;
      final int _cursorIndexOfCreatedAt = 6;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<MaterialEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final MaterialEntity _item_1;
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
          _item_1 = new MaterialEntity(_tmpId,_tmpName,_tmpQuantity,_tmpUnit,_tmpImagePath,_tmpNotes,_tmpCreatedAt);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
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
    _stringBuilder.append("SELECT `tags`.`id` AS `id`,`tags`.`name` AS `name`,`tags`.`colorHex` AS `colorHex`,_junction.`productId` FROM `product_tag_cross_ref` AS _junction INNER JOIN `tags` ON (_junction.`tagId` = `tags`.`id`) WHERE _junction.`productId` IN (");
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
      // _junction.productId;
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
