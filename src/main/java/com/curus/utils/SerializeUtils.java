package com.curus.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class SerializeUtils {
    private static Log logger = LogFactory.getLog(SerializeUtils.class);
    private static final String ENCODING = "ISO-8859-1";
    public static byte[] serialize2bytes(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    public static String serialize2string(Object object) {
        if ( object == null ) return null;
        byte[] bytes = serialize2bytes(object);
        if ( bytes == null ) return null;
        else try {
            return new String(bytes, ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    public static Object unserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    public static Object unserialize(String ostr) {
        if (ostr == null) return null;
        try {
            return unserialize(ostr.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }
}
