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

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `materials` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `imagePath` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `colorHex` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `material_tag_cross_ref` (`materialId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`materialId`, `tagId`), FOREIGN KEY(`materialId`) REFERENCES `materials`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_material_tag_cross_ref_materialId` ON `material_tag_cross_ref` (`materialId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_material_tag_cross_ref_tagId` ON `material_tag_cross_ref` (`tagId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '347cb57fd1874d1bd07bdde856717168')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `materials`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `material_tag_cross_ref`");
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
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "347cb57fd1874d1bd07bdde856717168", "2def1a8fc1b8367de6693a9e07543bf6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "materials","tags","material_tag_cross_ref");
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
}
