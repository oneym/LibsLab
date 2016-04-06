package com.oneym.libslab.utils.media;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.oneym.libslab.exception.NotInitException;
import com.oneym.libslab.utils.Constants;
import com.oneym.libslab.utils.common.Density;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.io.UtilsIO;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.widget.OPopupMenu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * @author oneym oneym@sina.cn
 * @since 20151214134308
 */
public class UtilsMedia {

    /**
     * 字符串中包含扩展名
     *
     * @param str    文件名字符串
     * @param suffix 文件的目标扩展名
     * @return true包含目标扩展名，false不包含目标扩展名
     */
    public static boolean isContainsSuffix(String str, String suffix) {
        boolean isContainsSuffix = false;
        if (str.contains(".")) {
            String suf = "." + str.substring(str.lastIndexOf(".") + 1);
            if (!UtilsString.isEmptyString(suf))
                isContainsSuffix = suf.toLowerCase().equals(suffix.toLowerCase());
        }
        return isContainsSuffix;
    }

    /**
     * 给定的content_type是一个图片类型
     *
     * @param content_type http-content-type
     * @return false字符串代表不是图片类型，是图片类型就直接返回后缀名
     */
    public static String isImage(String content_type) {
        String suffix = "false";
        for (int i = 0; i < Constants.IMG_CONTENT_TYPE[0].length; i++) {
            if (Constants.IMG_CONTENT_TYPE[0][i].equals(content_type.toLowerCase())) {
                suffix = Constants.IMG_CONTENT_TYPE[1][i];
                break;
            }
        }
        return suffix;
    }

    /**
     * 给定的路径是一个文件
     *
     * @param path 已知或者未知后缀名的文件绝对路径
     * @return false字符串代表不是文件，是文件就直接返回包含后缀名的文件绝对路径
     */
    public static String isImageExist(String path) {
        String path_ = "false";
        String temp_path_ = "";

        for (int i = 0; i < Constants.IMG_CONTENT_TYPE[1].length; i++) {
            if (!path.contains(".")) {
                temp_path_ = path + Constants.IMG_CONTENT_TYPE[1][i];
                File file = new File(temp_path_);
                if (file.exists()) {
                    path_ = temp_path_;
                    break;
                }
            } else {
                temp_path_ = "." + path.substring(path.lastIndexOf(".") + 1).toLowerCase();
                if (Constants.IMG_CONTENT_TYPE[1][i].equals(temp_path_)) {
                    if (new File(path).exists()) {
                        path_ = path;
                        break;
                    }
                }
            }
        }
        return path_;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param activity Activity
     * @param bgAlpha  透明度0.0-1.0,An alpha of 1.0 means fully opaque and 0.0 means fully transparent
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }


    /**
     * Bitmap另存为png个格式的byte数组
     *
     * @param bitmap Bitmap
     * @return byte[]
     */
    public static byte[] bitmap2Bytes_png(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            baos.flush();
            baos.close();
        } catch (IOException e) {
            Log.out(e);
        }
        return baos.toByteArray();
    }

    /**
     * Bitmap另存为jpg个格式的byte数组
     *
     * @param bitmap Bitmap
     * @return byte[]
     */
    public static byte[] bitmap2Bytes_jpg(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();
            baos.close();
        } catch (IOException e) {
            Log.out(e);
        }
        return baos.toByteArray();
    }

    /**
     * bitmap转为base64
     *
     * @param bitmapBytes
     * @return
     */
    public static String bitmapToBase64(byte[] bitmapBytes) {
        String result = "";
        if (bitmapBytes != null)
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data StringOfBase64
     * @return Bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 从底部弹出一个菜单，用户选择从相册或者相机获取图片<br/>
     * 1、需要在onActivityResult方法中调用onActivityResultForImage来获得bitmap或者onActivityResultForImagePath来获得图片路径,根据requestCode和v的ID的对比区分图片<br/>
     * 2、需要在manifest文件中添加{@code <activity
     * android:name="me.nereo.multi_image_selector.MultiImageSelectorActivity"
     * android:configChanges="orientation|screenSize"
     * android:screenOrientation="portrait" />}<br/>
     * 注意：这个类不要用单例模式会收不到图片的
     *
     * @param activity    活动
     * @param v           点击的view
     * @param mSelectPath 已选择的图片,允许为null
     * @param maxAmount   最多选择的图片数量，默认3张
     */
    public static void getImageFrom(@NonNull final Activity activity, @NonNull final View v, final ArrayList<String> mSelectPath, @NonNull final int maxAmount) {
        try {

            if (null == activity || null == v) {
                throw new IllegalArgumentException("参数为空");
            }

            final ArrayList list = new ArrayList();
            list.add("从相册选择");
            list.add("拍照");

            OPopupMenu.getInstance().load(list, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if ("从相册选择".equals(list.get(position))) {
//                        activity.startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
//                                        .addCategory(Intent.CATEGORY_OPENABLE)
//                                        .setType("image/*")
//                                        .putExtra("return-data", true)
//                                , v.getId());

                        Intent intent = new Intent(activity.getApplicationContext(), MultiImageSelectorActivity.class);
                        // 是否显示拍摄图片
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                        // 最大可选择图片数量
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxAmount > 0 ? maxAmount : 3);
                        // 选择模式
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                        // 默认选择
                        if (mSelectPath != null && mSelectPath.size() > 0) {
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                        }

                        activity.startActivityForResult(intent, v.getId());


                    } else if ("拍照".equals(list.get(position))) {
                        activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), v.getId());
                    }
                    OPopupMenu.getInstance().dismiss();
                }
            }).show(v);
        } catch (NotInitException e) {
            Log.out(e);
        }
    }

    /**
     * 获取onActivityResult回调中的图片路径,与{@link UtilsMedia#getImageFrom}方法配合使用
     *
     * @param resolver 在Activity中直接传入getContentResolver()
     * @param data     onActivityResult回调传入的Intent参数
     * @return 图片路径，单个路径可能存在为空的情况
     */
    public static ArrayList<String> onActivityResultForImagePath(ContentResolver resolver, Intent data) {
        ArrayList<String> pathes = new ArrayList<>();
        try {

            if (null == resolver || null == data)
                throw new NullPointerException("onActivityResultForImagePath: 传入参数为空");

            if (null != data.getParcelableExtra("data")) {
                Log.out("相机方式");
                Bitmap bitmap = data.getParcelableExtra("data");
                String path = UtilsIO.saveBitmap2png(bitmap, Environment.DIRECTORY_DCIM);
                if (!UtilsString.isEmptyString(path)) {
                    pathes.add(path);
                } else {
                    Log.out("相机方式获取失败");
                }
            } else if (!UtilsString.isEmptyString(data.getDataString()) && null != MediaStore.Images.Media.getBitmap(resolver, data.getData())) {
                Log.out("相册方式");
                //bitmap = MediaStore.Images.Media.getBitmap(resolver, data.getData());
                pathes.add(data.getDataString());
            } else {//多张图片
                Log.out("相册方式(多选)");
                pathes = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            }

//            if (null == bitmap)
//                throw new NullPointerException(TAG + ".onActivityResultForImagePath: bitmap为空，请检查代码");

        } catch (IOException e) {
            Log.out(e);
        }
        return pathes;
    }

    /**
     * 获取onActivityResult回调中的图片,与{@link UtilsMedia#getImageFrom}方法配合使用
     *
     * @param activity Activity
     * @param data     onActivityResult回调传入的Intent参数
     * @param amount   预期需要获得多少张图片（这个预期可能无法满足）
     * @return 可能没有一张图片
     */
    public static ArrayList<Bitmap> onActivityResultForImage(Activity activity, Intent data, int amount) {
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        ArrayList<String> pathes = onActivityResultForImagePath(activity.getContentResolver(), data);
        int amountWeNeed = pathes.size() > amount ? amount : pathes.size();
        for (int i = 0; i < amountWeNeed; i++)
            bitmaps.add(UtilsIO.readImage2png(pathes.get(i)));
        return bitmaps;
    }


    /**
     * 获取指定Activity的截屏
     *
     * @param activity 目标activity
     * @return bitmap
     */
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.out("statusBarHeight=" + statusBarHeight);

        // 获取屏幕长和高
        int width = Density.getScreenWH(activity)[0];
        int height = Density.getScreenWH(activity)[1];
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    /**
     * 保存到sdcard
     *
     * @param b    图片
     * @param path 保存的路径，不包含文件名
     * @return 返回保存的绝对路径，可能为空，为空表示存储错误
     */
    public static String saveBitmap2png(Bitmap b, String path) {
        return UtilsIO.saveBitmap2png(b, path);
    }

    /**
     * 获得圆角图片的方法
     *
     * @param context 上下文
     * @param bitmap  需要修改的图片
     * @param roundDp 圆角大小,单位是dp
     * @return 改好的图片, 为null时是参数错误<br/>
     * <a href="http://blog.csdn.net/liranke/article/details/42190503">出处</a>
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float roundDp) {

        if (roundDp < 0.1f || null == bitmap) {
            Log.out("参数错误(bitmap||roundDp)");
            return null;
        }

        float roundPx = Density.dip2px(context, roundDp);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获得圆角图片的方法(圆角为8db)
     *
     * @param context 上下文
     * @param bitmap  需要修改的图片
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap) {
        return getRoundedCornerBitmap(context, bitmap, 8f);
    }

    /**
     * 获得带倒影的图片方法
     *
     * @param bitmap 需要修改的图片
     * @return 改好的图片<br/>
     * <a href="http://blog.csdn.net/liranke/article/details/42190503">出处</a>
     */
    public static Bitmap getReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

}
