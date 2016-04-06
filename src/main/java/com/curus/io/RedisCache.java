package com.curus.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by stupid-coder on 9/3/16.
 */
public class RedisCache {

    static private Log logger = LogFactory.getLog(RedisCache.class);

    static private String ADDR = "123.57.227.101";
    static private int PORT = 6379;
    static private String PREFIX = "CURUS";
    private static int expire = 7*24*3600;
    private static int MAX_TOTAL = 8;
    private static int MAX_IDLE = 8;

    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;

    private static boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
            logger.info("Success to Static Initilization the Jedis");
        } catch ( Exception e ) {
            logger.warn("Failure to Static Initilization the Jedis");
        }
    }

    static String getKey( String key ) {
        return String.format("%s_%s",PREFIX,key);
    }

    static public void Add(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(getKey(key),value);
        jedis.expire(getKey(key),expire);
        jedis.close();
    }

    static public String Get(String key) {
        Jedis jedis = jedisPool.getResource();
        String value = jedis.get(getKey(key));
        jedis.close();
        return value;
    }

    static public void Delete(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(getKey(key));
        jedis.close();
    }

    static public String DeleteAndGet(String key) {
        Jedis jedis = jedisPool.getResource();
        String k = getKey(key);
        String v = jedis.get(k);
        if (v != null) jedis.del(k);
        jedis.close();
        return v;
    }

}
