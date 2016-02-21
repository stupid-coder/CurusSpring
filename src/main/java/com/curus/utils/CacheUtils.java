package com.curus.utils;

import com.curus.io.Cache;
import com.curus.utils.constant.CateConst;
import com.curus.utils.validate.CateValidate;
import org.apache.commons.lang.RandomStringUtils;

import java.util.UUID;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class CacheUtils {

    private static final int codeLen = 6;

    public static String getCode() {
        return RandomStringUtils.random(codeLen, false, true);
    }

    public static String codeCacheKey(String prefix, String phone) {
        return String.format("%s_%s",prefix,phone);
    }

    public static String codeCacheKey(String prefix, String token, String phone) {
        return String.format("%s_%s_%s",prefix,token,phone);
    }

    public static String getCodeCacheKey(String cate, String token, String phone) {
        int cateIdx = CateValidate.getIdx(cate);
        if (cateIdx == CateConst.IMODIFY_PHONE || cateIdx == CateConst.IADD_PATIENT)
            return codeCacheKey(cate, token, phone);
        else if (cateIdx == CateConst.IREGISTER || cateIdx == CateConst.IFORGET_PASSWD )
            return codeCacheKey(cate, phone);
        else return null;
    }

    public static void putCode2Cache(String cate, String token, String phone, String code) {
        String ckey = getCodeCacheKey(cate, token, phone);
        if (ckey != null)
            Cache.Add(ckey, code);
    }

    public static String getCode4Cache(String cate, String token, String phone) {
        String ckey = getCodeCacheKey(cate, token, phone);
        if (ckey != null) return Cache.Get(ckey);
        else return null;
    }

    public static void deleteCode4Cache(String cate, String token,String phone) {
        String ckey = getCodeCacheKey(cate, token, phone);
        if ( ckey != null)
            Cache.Delete(ckey);
    }



    public static String getToken() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static void putObject2Cache(String key, Object o) {
        Cache.Add(key, SerializeUtils.serialize2string(o));
    }
    public static String putObject2Cache(Object o) { String token = getToken(); putObject2Cache(token,o); return token;}

    public static Object getObject4Cache(String key) {
        return SerializeUtils.unserialize(Cache.Get(key));
    }

    public static void deleteObject4Cache(String key) {
        Cache.Delete(key);
    }

    public static Object deleteObjectAndGet4Cache(String key) {
        return SerializeUtils.unserialize(Cache.DeleteAndGet(key));
    }
}
