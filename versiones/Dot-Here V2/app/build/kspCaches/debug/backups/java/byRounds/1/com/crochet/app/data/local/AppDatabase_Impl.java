package com.crochet.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.crochet.app.data.local.dao.MaterialDao;
import com.crochet.app.data.local.dao.MaterialDao_Impl;
import com.crochet.app.data.local.dao.ProductDao;
import com.crochet.app.data.local.dao.ProductDao_Impl;
import com.crochet.app.data.local.dao.TagDao;
import com.crochet.app.data.local.dao.TagDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MaterialDao _materialDao;

  private volatile TagDao _tagDao;

  private volatile ProductDao _productDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `materials` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `imagePath` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `colorHex` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `material_tag_cross_ref` (`materialId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`materialId`, `tagId`), FOREIGN KEY(`materialId`) REFERENCES `materials`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_material_tag_cross_ref_materialId` ON `material_tag_cross_ref` (`materialId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_material_tag_cross_ref_tagId` ON `material_tag_cross_ref` (`tagId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `price` REAL NOT NULL, `dimensions` TEXT, `frontImagePath` TEXT, `backImagePath` TEXT, `leftImagePath` TEXT, `rightImagePath` TEXT, `topImagePath` TEXT, `bottomImagePath` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `product_material_cross_ref` (`productId` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, PRIMARY KEY(`productId`, `materialId`), FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`materialId`) REFERENCES `materials`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_material_cross_ref_productId` ON `product_material_cross_ref` (`productId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_material_cross_ref_materialId` ON `product_material_cross_ref` (`materialId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `product_tag_cross_ref` (`productId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`productId`, `tagId`), FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tag_cross_ref_productId` ON `product_tag_cross_ref` (`productId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tag_cross_ref_tagId` ON `product_tag_cross_ref` (`tagId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bca1f3000924c3900eafa39b0f93e5ed')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `materials`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `material_tag_cross_ref`");
        db.execSQL("DROP TABLE IF EXISTS `products`");
        db.execSQL("DROP TABLE IF EXISTS `product_material_cross_ref`");
        db.execSQL("DROP TABLE IF EXISTS `product_tag_cross_ref`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMaterials = new HashMap<String, TableInfo.Column>(7);
        _columnsMaterials.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterials.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaterials = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMaterials = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMaterials = new TableInfo("materials", _columnsMaterials, _foreignKeysMaterials, _indicesMaterials);
        final TableInfo _existingMaterials = TableInfo.read(db, "materials");
        if (!_infoMaterials.equals(_existingMaterials)) {
          return new RoomOpenHelper.ValidationResult(false, "materials(com.crochet.app.data.local.entity.MaterialEntity).\n"
                  + " Expected:\n" + _infoMaterials + "\n"
                  + " Found:\n" + _existingMaterials);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(3);
        _columnsTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("colorHex", new TableInfo.Column("colorHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(1);
        _indicesTags.add(new TableInfo.Index("index_tags_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.crochet.app.data.local.entity.TagEntity).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsMaterialTagCrossRef = new HashMap<String, TableInfo.Column>(2);
        _columnsMaterialTagCrossRef.put("materialId", new TableInfo.Column("materialId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaterialTagCrossRef.put("tagId", new TableInfo.Column("tagId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaterialTagCrossRef = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMaterialTagCrossRef.add(new TableInfo.ForeignKey("materials", "CASCADE", "NO ACTION", Arrays.asList("materialId"), Arrays.asList("id")));
        _foreignKeysMaterialTagCrossRef.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tagId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMaterialTagCrossRef = new HashSet<TableInfo.Index>(2);
        _indicesMaterialTagCrossRef.add(new TableInfo.Index("index_material_tag_cross_ref_materialId", false, Arrays.asList("materialId"), Arrays.asList("ASC")));
        _indicesMaterialTagCrossRef.add(new TableInfo.Index("index_material_tag_cross_ref_tagId", false, Arrays.asList("tagId"), Arrays.asList("ASC")));
        final TableInfo _infoMaterialTagCrossRef = new TableInfo("material_tag_cross_ref", _columnsMaterialTagCrossRef, _foreignKeysMaterialTagCrossRef, _indicesMaterialTagCrossRef);
        final TableInfo _existingMaterialTagCrossRef = TableInfo.read(db, "material_tag_cross_ref");
        if (!_infoMaterialTagCrossRef.equals(_existingMaterialTagCrossRef)) {
          return new RoomOpenHelper.ValidationResult(false, "material_tag_cross_ref(com.crochet.app.data.local.entity.MaterialTagCrossRef).\n"
                  + " Expected:\n" + _infoMaterialTagCrossRef + "\n"
                  + " Found:\n" + _existingMaterialTagCrossRef);
        }
        final HashMap<String, TableInfo.Column> _columnsProducts = new HashMap<String, TableInfo.Column>(12);
        _columnsProducts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("price", new TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("dimensions", new TableInfo.Column("dimensions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("frontImagePath", new TableInfo.Column("frontImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("backImagePath", new TableInfo.Column("backImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("leftImagePath", new TableInfo.Column("leftImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("rightImagePath", new TableInfo.Column("rightImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("topImagePath", new TableInfo.Column("topImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("bottomImagePath", new TableInfo.Column("bottomImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProducts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProducts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProducts = new TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts);
        final TableInfo _existingProducts = TableInfo.read(db, "products");
        if (!_infoProducts.equals(_existingProducts)) {
          return new RoomOpenHelper.ValidationResult(false, "products(com.crochet.app.data.local.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProducts + "\n"
                  + " Found:\n" + _existingProducts);
        }
        final HashMap<String, TableInfo.Column> _columnsProductMaterialCrossRef = new HashMap<String, TableInfo.Column>(2);
        _columnsProductMaterialCrossRef.put("productId", new TableInfo.Column("productId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductMaterialCrossRef.put("materialId", new TableInfo.Column("materialId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProductMaterialCrossRef = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysProductMaterialCrossRef.add(new TableInfo.ForeignKey("products", "CASCADE", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        _foreignKeysProductMaterialCrossRef.add(new TableInfo.ForeignKey("materials", "CASCADE", "NO ACTION", Arrays.asList("materialId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProductMaterialCrossRef = new HashSet<TableInfo.Index>(2);
        _indicesProductMaterialCrossRef.add(new TableInfo.Index("index_product_material_cross_ref_productId", false, Arrays.asList("productId"), Arrays.asList("ASC")));
        _indicesProductMaterialCrossRef.add(new TableInfo.Index("index_product_material_cross_ref_materialId", false, Arrays.asList("materialId"), Arrays.asList("ASC")));
        final TableInfo _infoProductMaterialCrossRef = new TableInfo("product_material_cross_ref", _columnsProductMaterialCrossRef, _foreignKeysProductMaterialCrossRef, _indicesProductMaterialCrossRef);
        final TableInfo _existingProductMaterialCrossRef = TableInfo.read(db, "product_material_cross_ref");
        if (!_infoProductMaterialCrossRef.equals(_existingProductMaterialCrossRef)) {
          return new RoomOpenHelper.ValidationResult(false, "product_material_cross_ref(com.crochet.app.data.local.entity.ProductMaterialCrossRef).\n"
                  + " Expected:\n" + _infoProductMaterialCrossRef + "\n"
                  + " Found:\n" + _existingProductMaterialCrossRef);
        }
        final HashMap<String, TableInfo.Column> _columnsProductTagCrossRef = new HashMap<String, TableInfo.Column>(2);
        _columnsProductTagCrossRef.put("productId", new TableInfo.Column("productId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTagCrossRef.put("tagId", new TableInfo.Column("tagId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProductTagCrossRef = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysProductTagCrossRef.add(new TableInfo.ForeignKey("products", "CASCADE", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        _foreignKeysProductTagCrossRef.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tagId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProductTagCrossRef = new HashSet<TableInfo.Index>(2);
        _indicesProductTagCrossRef.add(new TableInfo.Index("index_product_tag_cross_ref_productId", false, Arrays.asList("productId"), Arrays.asList("ASC")));
        _indicesProductTagCrossRef.add(new TableInfo.Index("index_product_tag_cross_ref_tagId", false, Arrays.asList("tagId"), Arrays.asList("ASC")));
        final TableInfo _infoProductTagCrossRef = new TableInfo("product_tag_cross_ref", _columnsProductTagCrossRef, _foreignKeysProductTagCrossRef, _indicesProductTagCrossRef);
        final TableInfo _existingProductTagCrossRef = TableInfo.read(db, "product_tag_cross_ref");
        if (!_infoProductTagCrossRef.equals(_existingProductTagCrossRef)) {
          return new RoomOpenHelper.ValidationResult(false, "product_tag_cross_ref(com.crochet.app.data.local.entity.ProductTagCrossRef).\n"
                  + " Expected:\n" + _infoProductTagCrossRef + "\n"
                  + " Found:\n" + _existingProductTagCrossRef);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "bca1f3000924c3900eafa39b0f93e5ed", "c865cb87d2fe7669a86fd9163f7ab9bf");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "materials","tags","material_tag_cross_ref","products","product_material_cross_ref","product_tag_cross_ref");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `materials`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `material_tag_cross_ref`");
      _db.execSQL("DELETE FROM `products`");
      _db.execSQL("DELETE FROM `product_material_cross_ref`");
      _db.execSQL("DELETE FROM `product_tag_cross_ref`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MaterialDao.class, MaterialDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MaterialDao materialDao() {
    if (_materialDao != null) {
      return _materialDao;
    } else {
      synchronized(this) {
        if(_materialDao == null) {
          _materialDao = new MaterialDao_Impl(this);
        }
        return _materialDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }
}
