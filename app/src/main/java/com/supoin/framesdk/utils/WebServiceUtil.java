package com.supoin.framesdk.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @Author 安仔夏天勤奋
 * Create Date is  2019/8/19
 * Des  webService的访问
 */
public class WebServiceUtil {

    /**
     * webService的访问
     * @param soapObject 接口参数配置
     * @param methodName 接口方法名
     * @param url  接口RUL
     * @param soapAction 与后端对应的Action
     * @return 返回接口数据
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static SoapObject accessWcf(SoapObject soapObject, String methodName, String url, String soapAction) throws IOException, XmlPullParserException {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(soapObject);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;

        HttpTransportSE transportSE = new HttpTransportSE(url);
        transportSE.debug = true;//使用调式功能

        transportSE.call(soapAction + methodName, envelope);
        return (SoapObject) envelope.bodyIn;
    }



}
