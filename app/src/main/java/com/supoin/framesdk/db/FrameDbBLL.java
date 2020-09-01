package com.supoin.framesdk.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.supoin.framesdk.configure.FrameGlobalVariable;

/**
 * 数据库的注册表增加、删除操作
 */
public class FrameDbBLL {

    private FrameDbBLL(){}

    private static class FrameDbInstance{
        private static final FrameDbBLL INSTANCE = new FrameDbBLL();
    }

    public static FrameDbBLL getInstance() {
        return FrameDbInstance.INSTANCE;
    }

    public boolean IsExist()
    {
        String sql = "select RegNo from T_Register where DeviceID='"+ FrameGlobalVariable.DeviceID +"'";

        Cursor cursor = FrameGlobalVariable.dbFrameHelper.query(sql);

        if (cursor.moveToNext()) {
           return true;
        }
        return false;
    }

    public boolean insert(String regNo)
    {
        ContentValues values = new ContentValues();
        values.put("DeviceID", FrameGlobalVariable.DeviceID);
        values.put("RegNo", regNo);

        return FrameGlobalVariable.dbFrameHelper.insert("T_Register", values) > 0;
    }

    public boolean delete()
    {
        return FrameGlobalVariable.dbFrameHelper.delete("T_Register", "", null) >0;
    }
}
