package com.supoin.framesdk.configure;

import com.blankj.utilcode.util.SDCardUtils;
import com.supoin.framesdk.db.DBFrameHelper;

/**
 * 全局变量
 */
public class FrameGlobalVariable {

	//更新APK的文件名
	public static String appApkName="com.supoin.frame.apk";
	//更新版本号标志号
	public static String productNum ="AM001\\Frame\\Frame001";

	//下载文件路径
	public static String projectName = "Frame";
	//项目路径
	public static String projectPath = SDCardUtils.getSDCardPathByEnvironment() + "/" + projectName;
	//更新APP路径
	public static String updatePath = projectPath + "/Update/";
	//数据库存储路径
	public static String databasePath = projectPath + "/Database/";
	//异常崩溃路径
	public static String crashPath = projectPath + "/Crash/";
	//系统日志路径
	public static String logPath = projectPath + "/Log";
	//FTP导出路径
	public static String exportPath = projectPath + "/ExportDataFile/";

	//设备序列号
	public static String DeviceID = "";
	public static String IMEI = "";
	public static String BrandName = "";//设备品牌
	public static String Manufacturer = "";//生家厂家
	public static String Model = "";//产品型号
	//蓝牙地址
	public static String BluetoothAddress = "";
	//框架数据库实例
	public static DBFrameHelper dbFrameHelper;
}
