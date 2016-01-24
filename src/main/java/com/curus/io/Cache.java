package com.curus.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 23/1/16.
 */
//TODO redis
public class Cache {
    static private Log logger = LogFactory.getLog(Cache.class);

    static private Map<String,String> cache = new HashMap<String,String>();

    static public void Add(String key, String value) {
        synchronized (cache) {
            cache.put(key,value);
        }
    }

    static public String Get(String key) {
        String value = null;
        synchronized (cache) {
            if (cache.containsKey(key)) value = cache.get(key);
        }
        return value;
    }

    static public void Delete(String key) {
        synchronized (cache) {
            if ( cache.containsKey(key) ) cache.remove(key);

        }
    }

    static public String DeleteAndGet(String key) {
        synchronized (cache) {
            if (cache.containsKey(key)) return cache.remove(key);
            else return null;
        }
    }
}
