package com.lyj.framwork.log.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.lyj.framwork.log.Logger;

/**
 * 
 * [文件处理工具]<BR>
 * 
 * @author 周昕
 * @version [EasierBaseLine_Android, 2013-1-28]
 */
public class FileUtil
{
    private static final String TAG = "FileUtil";

    /**
     * 可保存的�??��的文件数�??
     */
    private static final int MAX_FILE_COUNT = 10;

    private static final int WAIT_TIME = 200;

    private FileUtil()
    {
    }

    /**
     * fore delete a file,thread safe.
     * 
     * @param file file
     * @return del result
     */
    public static boolean forceDeleteFile(File file)
    {
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount < MAX_FILE_COUNT)
        {
            tryCount++;
            result = file.delete();
            if (!result)
            {
                try
                {
                    synchronized (file)
                    {
                        file.wait(WAIT_TIME);
                    }
                }
                catch (InterruptedException e)
                {
                    Logger.e("FileUtil.forceDeleteFile",
                        "",
                        e);
                }
            }
        }
        Logger.v("FileUtil.forceDeleteFile",
            "tryCount = " + tryCount);
        return result;
    }

    /**
     * read strings for a file in /data/data/package/filename
     * 
     * @param context context
     * @param file file
     * @return strings for a file in /data/data/package/filename
     */
    public static String read(Context context, String file)
    {
        String data = "";
        try
        {
            FileInputStream stream = context.openFileInput(file);
            StringBuffer sb = new StringBuffer();
            int c = stream.read();
            while (c != -1)
            {
                sb.append((char) c);
                c = stream.read();
            }
            stream.close();
            data = sb.toString();

        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
        catch (IOException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
        return data;
    }

    /**
     * write strings to a file in /data/data/package/filename
     * 
     * @param context context
     * @param file file
     * @param msg msg
     */
    @SuppressLint("WorldWriteableFiles")
    public static void write(Context context, String file, String msg)
    {
        try
        {
            FileOutputStream stream = context.openFileOutput(file,
                Context.MODE_WORLD_WRITEABLE);
            stream.write(msg.getBytes());
            stream.flush();
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
        catch (IOException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
    }

    /**
     * 专门用来关闭可关闭的�?
     * 
     * @param beCloseStream �?要关闭的�?
     * @return 已经为空或�?�关闭成功返回true，否则返回false
     */
    public static boolean closeStream(java.io.Closeable beCloseStream)
    {
        if (beCloseStream != null)
        {
            try
            {
                beCloseStream.close();
                return true;
            }
            catch (IOException e)
            {
                Logger.e(TAG,
                    "close stream error",
                    e);
                return false;
            }
        }
        return true;
    }

    public static void saveFile(String path, byte[] data)
    {
        try
        {
            File file = new File(path);
            File parent = file.getParentFile();
            if (!parent.exists())
            {
                parent.mkdirs();
            }
            file.createNewFile();

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data);
            stream.flush();
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
        catch (IOException e)
        {
            Logger.e(TAG,
                e.getMessage());
        }
    }

    /**
     * 
     * [�?句话功能�?述]<BR>
     * [功能详细描述]
     * 
     * @param path
     * @author wujian
     */
    public static void deleteFiel(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            file.delete();
        }
    }
    
    /**
     * 文件复制
     * 
     * @param target 目标路径
     * @param source 原始路径
     * 
     * @return true:复制成功；false：复制失�?
     */
    public static boolean copyFile(String target, String source)
    {
        if (TextUtils.isEmpty(target) || TextUtils.isEmpty(source))
        {
            return false;
        }
        File sourceFile = new File(source);
        if (!sourceFile.exists())
        {
            return false;
        }
        FileOutputStream fOut = null;
        FileInputStream fIn = null;
        try
        {
            // 创建新文�?
            File targetFile = new File(target);
            File parent = targetFile.getParentFile();
            if (!parent.exists())
            {
                parent.mkdirs();
            }
            targetFile.createNewFile();
            // copy源文�?
            fOut = new FileOutputStream(target);
            fIn = new FileInputStream(source);
            byte[] buffer = new byte[1024];
            int readLine = 0;
            while ((readLine = fIn.read(buffer,
                0,
                1024)) >= 0)
            {
                fOut.write(buffer,
                    0,
                    readLine);
            }
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Logger.e(TAG,
                " Exception : " + e);
            return false;
        }
        finally
        {
            try
            {
                if (fOut != null)
                {
                    fOut.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (fIn != null)
                {
                    fIn.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    /**
     * 文件路径配置
     * 
     * 包括图片文件，附件文件的本地路径
     *
     */
    public static class FilePathConfig
    {
        /** 图片类型后缀 */
        public final static String SUFFIX_PNG = ".png";
        public final static String SUFFIX_JPG = ".jpg";
        
//        /**
//         * 获取下载图片文件本地路径
//         * 
//         * TODO �?要隐�?
//         * 
//         * @param photoId 图片Id
//         * @return 图片本地路径
//         */
//        public static String getDownLoadImgPath(String photoId)
//        {
//            if (TextUtils.isEmpty(photoId))
//            {
//                return "";
//            }
//            return Environment.getExternalStorageDirectory().getPath() + "/yonyou/.download/"
//                + Config.getInstance().getUserId() + "/" + photoId;
//        }
        
        /**
         * 获取图片文件本地保存路径
         * 
         * @param photoId 图片Id
         * @return 图片本地路径
         */
        public static String getSaveImgPath(String photoId)
        {
            if (TextUtils.isEmpty(photoId))
            {
                return "";
            }
            return Environment.getExternalStorageDirectory().getPath() + "/yonyou/save/"
                 + photoId + SUFFIX_PNG;
        }
        
        /**
         * 获取图片上传路径
         * 
         * TODO 暂不�?要后�?类型�?
         * 
         * @param photoType 图片类型：png、jpg�?
         * @return
         */
//        public static String getUploadImgPath(String photoType)
//        {
//            return Environment.getExternalStorageDirectory().getPath() + "/yonyou/.upload/"
//                + Config.getInstance().getUserId() + "/"
//                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
////                + photoType;
//        }
    }
}
