package com.example.administrator.android_sta_vod.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/13 0013.
 * 文件下载工具类
 */

public class File_utils {
    private String SD_PATH;

    public String getSD_PATH() {
        return SD_PATH;
    }
    public File_utils() {
        //得到当前外部存储设备的目录
        SD_PATH  = Environment.getExternalStorageDirectory() + "/";
    }
    public File createSDFile(String fileName) throws IOException {
        File file  = new File(SD_PATH + fileName);
        file.createNewFile();
        return file;
    }

    public File createSDDir(String dirName){
        File dir = new File(SD_PATH + dirName);
        dir.mkdir();
        return dir;
    }

    public boolean isFileExist(String fileName){
        File file = new File(SD_PATH + fileName);
        return file.exists();
    }

    public File write2SDFromInput(String path,String fileName,byte[] buf){
        File file=null;
        FileOutputStream output = null;
        try {
            createSDDir(path);
            file  = new File(SD_PATH +path+"/"+ fileName);
                file.createNewFile();
                output = new FileOutputStream(file,true);
                output.write(buf,0,buf.length);
                //清掉缓存
                output.flush();
        } catch (Exception e) {
            Log.e("write2SDFromInput", e.getMessage());
        } finally {
            try {
                if (output != null){
                    output.close();
                }
            } catch (IOException ioe){
                Log.e("write2SDFromInput", ioe.getMessage());
            }
        }
        return file;
    }
}
