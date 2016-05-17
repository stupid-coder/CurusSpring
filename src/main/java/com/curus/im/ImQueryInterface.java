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
    public static String curus_passwd = "curus-robot";
    public static String curus_name = "CURUS";

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

    public static boolean CommonInterface(String post_uri, Map<String,String> entitys, String operation_msg) {
        try {
            String response = HttpClient(post_uri,entitys);
            JSONObject jo = JSONObject.parseObject(response);
            if ( "200".compareTo(jo.getString("code")) != 0 ) {
                logger.warn(String.format("Failure to %s - %s %s", operation_msg, entitys, response));
            } else {
                logger.info(String.format("Success to %s - %s %s", operation_msg, entitys, response));
                return true;
            }
        } catch ( Exception e ) {
            logger.warn(String.format("%s error - %s %s",operation_msg, entitys, e));
        }
        return false;
    }
    public static boolean Create(String accid, String token, String name) {
        Map<String,String> entitys = new HashMap<String,String>();
        entitys.put("accid",accid);
        entitys.put("token",token);
        if ( name != null ) entitys.put("name",name);
        return CommonInterface("https://api.netease.im/nimserver/user/create.action",entitys, "Create IM Account");
    }

    public static boolean Add(String accid, String faccid) {
        Map<String,String> entitys = new HashMap<String, String>();
        entitys.put("accid",accid);
        entitys.put("faccid",faccid);
        entitys.put("type","1");
        return CommonInterface("https://api.netease.im/nimserver/friend/add.action",entitys,"Add IM Friend");
    }

    public static boolean Delete(String accid, String faccid) {
        Map<String,String> entitys = new HashMap<String, String>();
        entitys.put("accid",accid);
        entitys.put("faccid",faccid);
        return CommonInterface("https://api.netease.im/nimserver/friend/delete.action", entitys, "Delete IM Friend");
    }

    public static boolean AddCurusRobot(String accid) {
        return Add(curus_accid, accid);
    }

    public static JSONObject GetIMFriends(String accid) {
        Map<String,String> entitys = new HashMap<String, String>();
        entitys.put("accid",accid);
        entitys.put("createtime",String.valueOf(TimeUtils.m2t(System.currentTimeMillis())));
        try {
            String response = HttpClient("https://api.netease.im/nimserver/friend/get.action",entitys);
            JSONObject jo = JSONObject.parseObject(response);
            if ( "200".compareTo(jo.getString("code")) != 0 ) {
                logger.warn(String.format("Failure to Get IM Friends - %s %s", entitys, response));
            } else {
                logger.info(String.format("Success to Get IM Friends - %s %s", entitys, response));
                return jo.getJSONObject("friends");
            }
        } catch ( Exception e ) {
            logger.warn(String.format("Get IM Friends error - %s %s", entitys, e));
        }
        return null;
    }

}
