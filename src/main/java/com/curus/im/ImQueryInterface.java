package com.curus.im;

import com.alibaba.fastjson.JSONObject;
import com.curus.utils.TimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stupid-coder on 5/4/16.
 */
public class ImQueryInterface {

    static Log logger = LogFactory.getLog(ImQueryInterface.class);
    static String appKey = "7162721a4e3c9566bcfbd7e2973c0dfd";
    static String appSecret = "0c31e207f766";

    public static String curus_accid = "curus-robot";

    public static String HttpClient(
            String url,
            Map<String,String> entitys
    ) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        String curTime = String.valueOf(TimeUtils.m2t(System.currentTimeMillis()));

        httpPost.addHeader("AppKey",appKey);
        httpPost.addHeader("Nonce",curTime);
        httpPost.addHeader("CurTime",curTime);
        httpPost.addHeader("CheckSum", CheckSumBuilder.getCheckSum(appSecret,curTime,curTime));
        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String,String> entry : entitys.entrySet() ) {
            nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        HttpResponse response = httpClient.execute(httpPost);

        return EntityUtils.toString(response.getEntity(),"utf-8");
    }

    public static Boolean Create(String accid, String token, String name) {
        Map<String,String> entitys = new HashMap<String,String>();
        entitys.put("accid",accid);
        entitys.put("token",token);
        if ( name != null && name.compareTo("") != 0) entitys.put("name",name);
        try {
            String response = HttpClient("https://api.netease.im/nimserver/user/create.action", entitys);
            JSONObject jo = JSONObject.parseObject(response);
            if ( "200".compareTo(jo.getString("code")) != 0 ) {
                logger.warn(String.format("Failure to Create Account - %s %s", entitys, response));
                return false;
            } else {
                logger.info(String.format("Success to Create Account - %s %s", entitys, response));
                return true;
            }
        } catch(Exception e) {
            logger.warn(String.format("Create IM Account error - %s %s", entitys,e));
            return false;
        }
    }

    public static Boolean Add(String accid, String faccid) {
        Map<String,String> entitys = new HashMap<String, String>();
        entitys.put("accid",accid);
        entitys.put("faccid",faccid);
        entitys.put("type","1");
        try {
            String response = HttpClient("https://api.netease.im/nimserver/friend/add.action", entitys);
            JSONObject jo = JSONObject.parseObject(response);
            if ( "200".compareTo(jo.getString("code")) != 0 ) {
                logger.warn(String.format("Failure to Add Friend - %s %s", entitys, response));
                return false;
            } else {
                logger.warn(String.format("Success to Add Friend - %s %s", entitys, response));
                return true;
            }
        } catch (Exception e) {
            logger.warn(String.format("Add IM Friend error - %s %s", entitys, e));
            return false;
        }
    }

    static {
        Create(curus_accid,curus_accid,curus_accid);
    }

}
