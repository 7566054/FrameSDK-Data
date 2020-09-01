package com.supoin.framesdk.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.supoin.framesdk.R;
import com.supoin.framesdk.configure.FrameGlobalConst;
import com.supoin.framesdk.configure.FrameGlobalVariable;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.io.InputStream;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/9/6
 * Des
 */
public class FtpService extends Service{
    public static FtpServer server = null;
    static String port;

    @SuppressLint("SdCardPath")
    private static String dirname;
    // ftp服务器配置文件路径
    private static String filename;

    @Override
    public void onCreate(){
        try {
            // 使用getString方法获得value，注意第2个参数是value的默认值
            port = "2221";
            dirname = Environment.getExternalStorageDirectory().getPath() + "/ftp";
            filename = dirname + "/raw/myusers.properties";

            /* Create Ftp Server */
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory factory = new ListenerFactory();

            // set the port of the listener
            factory.setPort(Integer.parseInt(port));

            // replace the default listener
            serverFactory.addListener("default", factory.createListener());

            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

            InputStream is = getResources().openRawResource(R.raw.myusers);

            File file = new File(filename);
            File destfile = new File(dirname);
            if (!destfile.exists()) {
                destfile.mkdirs();
            }

            FileIOUtils.writeFileFromIS(file, is);
            userManagerFactory.setFile(file);

            UserManager userManager = userManagerFactory.createUserManager();
            BaseUser userAdmin = (BaseUser) userManager.getUserByName("admin");
            userAdmin.setHomeDirectory(FrameGlobalVariable.exportPath);
            userManager.save(userAdmin);

            BaseUser userAnonymous = (BaseUser) userManager.getUserByName("anonymous");
            userAnonymous.setHomeDirectory(FrameGlobalVariable.exportPath);
            userManager.save(userAnonymous);

            serverFactory.setUserManager(userManager);

            // start the server
            server = serverFactory.createServer();
            server.start();
        } catch (FtpException e) {
            LogUtils.e(e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
