package com.curus.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 26/1/16.
 */
public class TypeUtils {

    public static Map<String,Object> getWhereHashMap( final String key1, final Object obj1) {
        return new HashMap<String, Object>() {{ put(key1,obj1);}};
    }

    public static Map<String, Object> getWhereHashMap( final String key1, final Object obj1,
                                                       final String key2, final Object obj2) {
        return new HashMap<String,Object>() {{ put(key1,obj1); put(key2,obj2); }};
    }

    public static Map<String, Object> getWhereHashMap( final String key1, final Object obj1,
                                                       final String key2, final Object obj2,
                                                       final String key3, final Object obj3) {
        return new HashMap<String,Object>() {{ put(key1,obj1); put(key2,obj2); put(key3,obj3); }};
    }

    public static Map<String, Object> getWhereHashMap( final String key1, final Object obj1,
                                                       final String key2, final Object obj2,
                                                       final String key3, final Object obj3,
                                                       final String key4, final Object obj4) {
        return new HashMap<String,Object>() {{
            put(key1,obj1); put(key2,obj2);
            put(key3,obj3); put(key4,obj4);}};
    }
}
