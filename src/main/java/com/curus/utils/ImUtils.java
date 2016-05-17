package com.curus.utils;

import com.curus.dao.CurusDriver;
import com.curus.im.ImQueryInterface;


/**
 * Created by stupid-coder on 5/17/16.
 */
public class ImUtils {

    public static boolean CreateIM(String accid,
                                   String name) {
        if (!ImQueryInterface.Create(accid,accid,name)) {
            return false;
        } else {
            ImQueryInterface.AddCurusRobot(accid);
        }
        return true;
    }

    public static boolean AddIM(String accid, String faccid) {
        ImQueryInterface.Add(accid,faccid);
        return ImQueryInterface.Add(faccid,accid);
    }

}
