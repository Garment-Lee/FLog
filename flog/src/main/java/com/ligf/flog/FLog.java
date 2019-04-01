package com.ligf.flog;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FLog {

    /**默认的Tag*/
    private static String DEFAULT_TAG = "FLog";

    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    private static SimpleDateFormat mFileDataFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**默认最大的文件大小10M*/
    private static int DEFAULT_MAX_FILE_SIZE = 2  * 1024;

    /**默认的文件保存目录*/
    private static String DEFAULT_FILE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FLog";

    /**显示Log信息的标志*/
    private static boolean mShowLogFlag;

    /**保存Log信息到文件的标志*/
    private static boolean mSaveToFileFlag;

    /**保存文件的路径*/
    private static String mSaveFileDirectory;

    /**完整的日志文件名称,包括文件完整的文件路径*/
    private static String mAbsoluteFileName;

    /**最大的日志文件大小*/
    private static int mMaxFileSize;

    public static void setShowLogFlag(boolean flag) {
        mShowLogFlag = flag;
    }

    public static void setSaveToFileFlag(boolean flag) {
        mSaveToFileFlag = flag;
    }

    public static void setSaveFilePath(String path) {
        mSaveFileDirectory = path;
    }

    public static void setMaxFileSize(int fileSize){
        mMaxFileSize = fileSize;
    }

    public static void init() {
        if (mSaveToFileFlag) {
            if (TextUtils.isEmpty(mSaveFileDirectory)) {
                mSaveFileDirectory = DEFAULT_FILE_DIRECTORY;
            }
            mAbsoluteFileName = getLatestFileName(mSaveFileDirectory);
        }
        if (mMaxFileSize == 0){
            mMaxFileSize = DEFAULT_MAX_FILE_SIZE;
        }
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (mShowLogFlag) {
            String[] logArr = getWrappedTagMsg(tag, msg);
            Log.v(logArr[0], logArr[1]);
        }
        if (mSaveToFileFlag) {
            saveToFile(tag, msg);
        }
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (mShowLogFlag) {
            String[] logArr = getWrappedTagMsg(tag, msg);
            Log.d(logArr[0], logArr[1]);
        }
        if (mSaveToFileFlag) {
            saveToFile(tag, msg);
        }
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (mShowLogFlag) {
            String[] logArr = getWrappedTagMsg(tag, msg);
            Log.i(logArr[0], logArr[1]);
        }
        if (mSaveToFileFlag) {
            saveToFile(tag, msg);
        }
    }

    public static void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (mShowLogFlag) {
            String[] logArr = getWrappedTagMsg(tag, msg);
            Log.w(logArr[0], logArr[1]);
        }
        if (mSaveToFileFlag) {
            saveToFile(tag, msg);
        }
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (mShowLogFlag) {
            String[] logArr = getWrappedTagMsg(tag, msg);
            Log.e(logArr[0], logArr[1]);
        }
        if (mSaveToFileFlag) {
            saveToFile(tag, msg);
        }
    }

    /**
     * 获取文件路径中的最新生成的文件名
     *
     * @param fileDirectory
     * @return
     */
    private static String getLatestFileName(String fileDirectory) {
        File file = new File(fileDirectory);
        File latestFile = null;
        int fileTime = 0;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null){
                for (File file1 : files) {
                    if (file1.lastModified() > fileTime){
                        latestFile = file1;
                    }
                }
            }
        }
        if (latestFile != null){
            return latestFile.getAbsolutePath();
        } else {
            return fileDirectory + File.separator + mFileDataFormat.format(new Date()).toString() + "flog" + ".txt";
        }
    }

    private static void saveToFile(String tag, String msg) {
        Log.i("FLog","saveToFile:" + mAbsoluteFileName);
        File saveFile = new File(mAbsoluteFileName);
       
        String[] logArr = getWrappedTagMsg(tag, msg);
        byte[] outputBytes = ("[" + logArr[0] + "]" + logArr[1]).getBytes();
        try {
            if (!saveFile.exists()) {
                if (saveFile.getParentFile() != null && !saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                saveFile.createNewFile();
                mAbsoluteFileName = saveFile.getAbsolutePath();
            } else if (saveFile.length() >= DEFAULT_MAX_FILE_SIZE){
                File newFile = generateNewFile(mSaveFileDirectory);
                mAbsoluteFileName = newFile.getAbsolutePath();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile, true);
            fileOutputStream.write(mDateFormat.format(new Date()).getBytes());
            fileOutputStream.write(outputBytes);
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成新的Log文件
     * @param fileDirectory
     * @return
     * @throws IOException
     */
    private static File generateNewFile(String fileDirectory) throws IOException {
        File newFile = new File(fileDirectory + File.separator + mFileDataFormat.format(new Date()).toString() + "flog" + ".txt");
        newFile.createNewFile();
        return newFile;
    }

    private static String[] getWrappedTagMsg(String tag, String msg) {
        String[] stringArr = new String[2];
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        stringArr[0] = TextUtils.isEmpty(tag) ? stackTraceElement.getClassName() : tag;
        stringArr[1] = "[ (" + stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber() + ") #" + stackTraceElement.getMethodName() + "]" + msg;
        return stringArr;
    }

    private static StackTraceElement getTargetStackTraceElement() {
        StackTraceElement targetStackTraceElement = null;
        boolean shouldTrace = false;
        StackTraceElement[] arrayElement = Thread.currentThread().getStackTrace();
        for (int i = 0; i < arrayElement.length; i++) {
            boolean isLogMethod = arrayElement[i].getClassName().equals(FLog.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTraceElement = arrayElement[i];
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTraceElement;
    }
}
