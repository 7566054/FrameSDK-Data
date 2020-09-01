package com.supoin.framesdk.service;

import android.util.Base64;

import com.supoin.framesdk.utils.WebServiceUtil;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * APP在线更新接口封装
 * Created by zwei on 2019/9/9.
 */

public class UpdateWebService implements IUpdateWebService {


    private static String NAMESPACE ="http://Supoin.MiddlePlatform";
    private static String URL = "http://download.supoin.com:8012/OutPortForAndroid/OutPortForAndroidService.svc";
    private static String SOAP_ACTION ="http://Supoin.MiddlePlatform/IOutPortForAndroidServiceContract/";

    @Override
    public String GetUpdateFileLenth(String UpLoadFileDir) throws IOException, XmlPullParserException {
        String result = null;
        String methodName = "GetUpdateFileLenth";
        SoapObject soapObject=new SoapObject(NAMESPACE, methodName);
        soapObject.addProperty("UpLoadFileDir", UpLoadFileDir);
        SoapObject soapObj =  WebServiceUtil.accessWcf(soapObject, methodName,URL,SOAP_ACTION);

        if (soapObj != null
                && SoapPrimitive.class.isInstance(soapObj.getProperty(0))) {
            SoapPrimitive soap = (SoapPrimitive) soapObj.getProperty(0);
            result = (String) soap.getValue();
        }
        return result;
    }

    @Override
    public String JsonVersionGetInfoOfLater(String CFullISN, String CName) throws IOException, XmlPullParserException {

        String result = null;
        String methodName = "JsonVersionGetInfoOfLater";

        SoapObject soapObject=new SoapObject(NAMESPACE, methodName);
        soapObject.addProperty("CFullISN",CFullISN);
        soapObject.addProperty("CName",CName);
        SoapObject soapObj = WebServiceUtil.accessWcf(soapObject, methodName,URL,SOAP_ACTION);

        if (soapObj != null) {
            if(SoapPrimitive.class.isInstance(soapObj.getProperty(0))){
                SoapPrimitive soap = (SoapPrimitive) soapObj.getProperty(0);
                result = (String) soap.getValue();
            }else{
                Object soap =  soapObj.getProperty(0);
                if(soap.toString().equals("anyType{}"))
                    result="latest";
            }
        }
        return result;
    }

    @Override
    public byte[] LoadFileByBlock(long IstartPost, String UpLoadFileDir) throws IOException, XmlPullParserException {
        byte[] result = null;
        String methodName = "LoadFileByBlock";

        SoapObject soapObject=new SoapObject(NAMESPACE, methodName);
        soapObject.addProperty("IstartPost", IstartPost);
        soapObject.addProperty("UpLoadFileDir", UpLoadFileDir);
        SoapObject soapObj = WebServiceUtil.accessWcf(soapObject, methodName,URL,SOAP_ACTION);
        if (soapObj != null
                && SoapPrimitive.class.isInstance(soapObj.getProperty(0))) {
            result = Base64.decode(soapObj.getProperty(0).toString(), Base64.DEFAULT);
        }
        return result;
    }
}
