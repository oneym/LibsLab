package com.oneym.libslab.utils.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.utils.time.UtilsTime;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author oneym
 * @since 20151211140833
 */
public class UtilsIO {

    /**
     * 传入一个json文件的完整路径获得这个文件的json对象
     *
     * @param fileFullPath 完整的路径
     * @return JSONObject
     */
    public static JSONObject getJsonObjectFromFile(final String fileFullPath) {
        JSONObject object = null;
        try {
            File file = new File(fileFullPath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder();
                String temp = "";
                while (!UtilsString.isEmptyString(temp = bf.readLine()))
                    sb.append(temp);

                Log.out("sb:" + sb.toString());
                JSONObject obj = new JSONObject(sb.toString());
                Log.out("obj=" + obj.toString());
                if (!UtilsString.isEmptyString(obj.toString()))
                    object = obj;
            } else {
                Object obj = "getJsonObjectFromFile-->文件不存在";
                Log.out(obj);
            }
        } catch (JSONException e) {
            Log.out(e);
        } catch (FileNotFoundException e) {
            Log.out(e);
        } catch (IOException e) {
            Log.out(e);
        } catch (Exception e) {
            Log.out(e);
        }
        return object;
    }

    /**
     * 传入一个json文件的完整路径获得这个文件的json对象（文件读完就删除了）
     *
     * @param fileFullPath 完整的路径
     * @param o            请传入null(空),只是为了重载这个方法作参数识别
     * @return JSONObject
     */
    public static JSONObject getJsonObjectFromFile(final String fileFullPath, final Object o) {
        JSONObject object = getJsonObjectFromFile(fileFullPath);
        deleteFileIfExist(fileFullPath);
        return object;
    }

    /**
     * 文件存在就删除
     *
     * @param fileFullPath 文件绝对路径
     */
    public static void deleteFileIfExist(final String fileFullPath) {
        File file = new File(fileFullPath);
        if (file.exists())
            file.delete();
    }

    /**
     * 删除指定目录下的文件
     *
     * @param dir 目录
     * @return 删除目录的大小
     */
    public static long deleteDirectory(final String dir) {
        long size = 0L;
        long l0 = 0L;
        long l1 = 0L;
        File file = new File(dir);
        Log.out("dir=" + dir);
        Log.out("file.exists()=" + file.exists());
        Log.out("file.isDirectory()=" + file.isDirectory());
        if (file.exists() && file.isDirectory()) {
            l0 = file.getFreeSpace();
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File path : files)
                    deleteFileIfExist(path.getAbsolutePath());
                l1 = file.getFreeSpace();

            }
        }
        if (l1 > l0) {
            size = l1 - l0;
        } else {
            size = 0L;
        }

        return size;
    }

    /**
     * 文件夹是存在且可读可写可执行
     *
     * @param path 路径或者包含文件名和扩展名的完整路径
     * @return true文件夹是存在且可读可写可执行，false其他情况
     */
    public static boolean isDiskCanReadAndWrite(String path) {
        boolean isCanReadAndWrite = true;
        File file = new File(path);
        if (null == file || !file.exists())
            file.mkdirs();
        if (!file.canRead() && !file.canWrite() && !file.canExecute())
            isCanReadAndWrite = false;
        return isCanReadAndWrite;
    }

    /**
     * 从文件读取一个图片到bitmap
     *
     * @param path 路径
     * @return
     */
    public static Bitmap readImage2png(String path) {
        Bitmap bitmap = null;
        try {
            Log.out("path=" + path);
            if (!UtilsString.isEmptyString(path)) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = getImageScale(path);
                bitmap = BitmapFactory.decodeFile(path, option);
            }
        } catch (Exception e) {
            Log.out(e);
        }

        return bitmap;
    }

    /**
     * 从文件读取一个图片到bitmap
     *
     * @param context 上下文
     * @param path    路径
     * @return
     */
    public static Bitmap readImage2png(Context context, String path) {
        Bitmap bitmap = null;
        try {
            if (!UtilsString.isEmptyString(path))
                bitmap = Picasso.with(context).load(path).get();
        } catch (IOException e) {
            Log.out(e);
        }
        return bitmap;
    }

    /**
     * 保存到sdcard
     *
     * @param b    图片
     * @param path 保存的路径，不包含文件名
     * @return 返回保存的绝对路径，可能为空，为空表示存储错误
     */
    public static String saveBitmap2png(Bitmap b, String path) {
        FileOutputStream fos = null;
        String path_ = "";
        try {
            path = UtilsString.toString(Environment.getExternalStorageDirectory()) + "/" + path;
            if (UtilsIO.isDiskCanReadAndWrite(path)) {
                String[] time = UtilsTime.millis2EyMdHms(System.currentTimeMillis());
                path += "/" + time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + ".png";
                fos = new FileOutputStream(path);
                if (null != fos) {
                    b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                    path_ = path;
                }
            }
        } catch (FileNotFoundException e) {
            Log.out(e);
        } catch (IOException e) {
            Log.out(e);
        }
        return path_;
    }

    /**
     * scale image to fixed height and weight,480*960
     *
     * @param imagePath 图片路径
     * @return
     * @see <a href="http://www.trinea.cn/android/android-image-outofmemory-bitmap-size-exceeds-vm-budget/">出处</a>
     */
    private static int getImageScale(String imagePath) {
        int IMAGE_MAX_WIDTH = 480;
        int IMAGE_MAX_HEIGHT = 960;
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale >= IMAGE_MAX_WIDTH || option.outHeight / scale >= IMAGE_MAX_HEIGHT) {
            scale *= 2;
        }
        return scale;
    }

}
