package com.supoin.framesdk.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by LYY on 2018/12/20.
 */

public class ZipUtils {
    public static final String TAG = "ZIP";

    public ZipUtils() {

    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static List<String> UnZipFolder(String zipFileString, String outPathString) throws Exception {
        List<String> unzipFileNameList = new ArrayList();

        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        FileOutputStream out =null;
        try {
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName); folder.mkdirs();
                } else {

                    Log.e(TAG,outPathString + File.separator + szName);
                    File file = new File(outPathString + File.separator + szName);
                    if (!file.exists()){
                        Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                        file.getParentFile().mkdirs(); file.createNewFile();
                    }

                    // 获取文件的输出流
                     out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len); out.flush();
                    }
                    out.close();
                    unzipFileNameList.add(file.getAbsolutePath());
                }
            }
            inZip.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inZip!=null){
                inZip.close();
            }
            if (out!=null){
                out.close();
            }
        }


        return unzipFileNameList;
    }











}