package com.oneym.demo.libslab.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oneym.libslab.exception.NotInitException;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.string.UtilsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android 数据库操作类
 *
 * @author oneym oneym@sina.cn
 * @since 20151126115243
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int ID = 0;

    private static final int WEEK = ID + 1;
    private static final int TIME = WEEK + 1;
    private static final int ISCHECKED = TIME + 1;

    private static final String DB_NAME = "_libslab_oneym";
    private static final int DB_VERSION = 3;
    private static final String TB_ALERT = "_libslab_alert_oneym";
    private static SQLiteHelper instance = null;
    private static Context cntext = null;
    private ContentValues cv = null;
    private SQLiteDatabase db = null;
    private Cursor cursor = null;

    private SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static void init(Context context) {
        cntext = context;
    }

    /**
     * 是null
     *
     * @return true，是null，false，不是null
     */
    public static boolean contextIsNull() {
        if (null == cntext)
            return true;
        return false;
    }

    public static SQLiteHelper getInstance() {
        try {
            if (null == instance) {
                if (null == cntext)
                    throw new NotInitException();
                instance = new SQLiteHelper(cntext);
            }
        } catch (NotInitException e) {
            Log.out(e);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_alert = "CREATE TABLE " + TB_ALERT
                + "(id INTEGER PRIMARY KEY NOT NULL,"
                + " week INTEGER,"
                + " time varchar(50),"
                + " isChecked varchar(50))";
        db.execSQL(sql_alert);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql_alert = "DROP TABLE IF EXISTS " + TB_ALERT;
        db.execSQL(sql_alert);
        onCreate(db);
    }

    /**
     * 增加一条记录
     *
     * @param week
     * @param time
     * @param isChecked
     * @return id
     */
    public long insert_alert(int week, String time, String isChecked) {
        long rowId = -1;
        try {
            db = this.getWritableDatabase();
            cv = new ContentValues();

            cv.put("week", week);
            cv.put("time", time);
            cv.put("isChecked", isChecked);
            rowId = db.insert(TB_ALERT, null, cv);
        } catch (Exception e) {
            Log.out(e);
        } finally {
            if (null != cv)
                cv.clear();
            if (null != db)
                db.close();
        }
        return rowId;
    }


    /**
     * 根据id删除一条记录
     *
     * @param id 索引
     * @return 影响的行数
     */
    public int deleteById_alert(int id) {
        int affected = 0;
        try {
            db = this.getReadableDatabase();
            String where = "id=?";
            String whereArgs[] = {Integer.toString(id)};
            affected = db.delete(TB_ALERT, where, whereArgs);
        } catch (Exception e) {
            Log.out(e);
        } finally {
            if (null != db)
                db.close();
        }
        return affected;
    }

    /**
     * 修改一条记录
     *
     * @param id        id
     * @param time      时间
     * @param week      周
     * @param isChecked 是否打开提醒
     * @return 影响的行数
     */
    public int update_alert(int id, String time, int week, String isChecked) {
        int affected = 0;
        try {
            db = this.getWritableDatabase();
            cv = new ContentValues();
            cv.put("time", time);
            cv.put("week", week);
            cv.put("isChecked", isChecked);
            String where = "id=?";
            String whereArgs[] = {Integer.toString(id)};
            affected = db.update(TB_ALERT, cv, where, whereArgs);
        } catch (Exception e) {
            Log.out(e);
        }
        return affected;
    }


    /**
     * 查询所有
     *
     * @return List<Map>
     */
    public List<Map> selectAll_alert() {
        List<Map> ls = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TB_ALERT, null, null, null, null, null, null);
            cursor.moveToPrevious();
            ls = new ArrayList<Map>();
            while (cursor.moveToNext()) {
                Map map = new HashMap();
                map.put("id", cursor.getInt(ID));
                map.put("week", cursor.getString(WEEK));
                map.put("time", cursor.getString(TIME));
                map.put("isChecked", cursor.getString(ISCHECKED));
                ls.add(map);
            }
        } catch (Exception e) {
            Log.out(e);
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }
        return ls;
    }


    public void show_alert() {
        List<Map> maps = SQLiteHelper.getInstance().selectAll_alert();
        Log.separator();
        Log.out("id\tweek\ttime\tisChecked");
        for (Map m : maps)
            Log.out(m.get("id") + "\t" + m.get("week") + "\t" + m.get("time") + "\t" + m.get("isChecked"));
        Log.separator();
    }

}
