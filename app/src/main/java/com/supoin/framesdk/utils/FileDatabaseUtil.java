package com.supoin.framesdk.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.io.File;

/**
 * 数据库存储位置重写
 * Created by zwei on 2019/9/1.
 */
public class FileDatabaseUtil extends ContextWrapper {
    private File file;
    private Context context;

    /**
     * @param base           上下文
     * @param file           文件存储目录
     */
    public FileDatabaseUtil(Context base, File file) {
        super(base);
        this.file = file;
        this.context = base;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public File getDatabasePath(String name) {

        if (file == null)
            return context.getDatabasePath(name);

        // 判断目录是否存在，不存在则创建该目录
        if (!file.exists())
            file.mkdirs();

        File addressFile = new File(file, name);
        if (addressFile == null)
            addressFile = context.getDatabasePath(name);
        return addressFile;
    }

    //重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    //Android 4.0会调用此方法获取数据库。
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

}
