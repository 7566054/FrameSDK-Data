package com.supoin.framesdk.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/17.
 */

public interface IUpdateWebService {
    String GetUpdateFileLenth(String UpLoadFileDir) throws IOException, XmlPullParserException;
    String JsonVersionGetInfoOfLater(String CFullISN, String CName) throws IOException, XmlPullParserException;
    byte[] LoadFileByBlock(long IstartPost, String UpLoadFileDir) throws IOException, XmlPullParserException;
}
