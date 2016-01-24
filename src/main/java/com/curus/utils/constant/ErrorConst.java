package com.curus.utils.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class ErrorConst {
    static public  final Integer IDX_SERVER_ERROR = 0;
    static private final String CODE_SERVER_ERROR = "-20000";
    static private final String  MSG_SERVER_ERROR = "server error";

    static public  final Integer IDX_INCVERIFYCODE_RROR = 1;
    static private final String CODE_INCVERIFYCODE_ERROR = "-20101";
    static private final String  MSG_INCVERIFYCODE_ERROR = "verify code error";

    static public  final Integer IDX_INVALIDEPHONE_ERROR = 2;
    static private final String CODE_INVALIDPHONE_ERROR = "-20102";
    static private final String  MSG_INVALIDPHONE_ERROR = "invalid phone";

    static public  final Integer IDX_INVALIDPASSWD_ERROR = 3;
    static private final String CODE_INVALIDPASSWD_ERROR = "-20103";
    static private final String  MSG_INVALIDPASSWD_ERROR = "invalid passwd";

    static public  final Integer IDX_DUPLPHONE_ERROR = 4;
    static private final String CODE_DUPLPHONE_ERROR = "-20104";
    static private final String  MSG_DUPLPHONE_ERROR = "duplicated phone";


    static public  final Integer IDX_USERNOTEXIST_ERROR = 5;
    static private final String CODE_USERNOTEXIST_ERROR = "-20105";
    static private final String  MSG_USERNOTEXIST_ERROR = "user not exist";

    static public  final Integer IDX_INCPASSWD_ERROR = 6;
    static private final String CODE_INCPASSWD_ERROR = "-20106";
    static private final String  MSG_INCPASSWD_ERROR = "incorrect passwd";

    static public  final Integer IDX_METHODNOTALLOWED_ERROR = 7;
    static private final String CODE_METHODNOTALLOWED_ERROR = "-20107";
    static private final String  MSG_METHODNOTALLOWED_ERROR = "method not allowed";

    static public  final Integer IDX_INVALIDNAME_ERROR = 8;
    static private final String CODE_INVALIDNAME_ERROR = "-20108";
    static private final String  MSG_INVALIDNAME_ERROR = "invalid name";

    static public  final Integer IDX_INVALIDID_ERROR = 9;
    static private final String CODE_INVALIDID_ERROR = "-20109";
    static private final String  MSG_INVALIDID_ERROR = "invalid id_number";

    static public  final Integer IDX_TOKENEXPIRED_ERROR = 10;
    static private final String CODE_TOKENEXPIRED_ERROR = "-20110";
    static private final String  MSG_TOKENEXPIRED_ERROR = "token has expired";

    static public  final Integer IDX_FORM_ERROR = 11;
    static private final String CODE_FORM_ERROR = "-20111";
    static private final String  MSG_FORM_ERROR = "form error";

    static public  final Integer IDX_PATIENTNOTEXIST_ERROR = 12;
    static private final String CODE_PATIENTNOTEXIST_ERROR = "-20112";
    static private final String  MSG_PATIENTNOTEXIST_ERROR = "patient not exist";

    static public  final Integer IDX_INVALIDJSON_ERROR = 13;
    static private final String CODE_INVALIDJSON_ERROR = "-20113";
    static private final String  MSG_INVALIDJSON_ERROR = "invalid json";

    static public  final Integer IDX_NOPERMISSION_ERROR = 14;
    static private final String CODE_NOPERMISSION_ERROR = "-20114";
    static private final String  MSG_NOPERMISSION_ERROR = "has no permission";

    static public  final Integer IDX_INVALIDTOKEN_ERROR = 15;
    static private final String CODE_INVALIDTOKEN_ERROR = "-20115";
    static private final String  MSG_INVALIDTOKEN_ERROR = "invalid token";

    static public  final Integer IDX_INVALIDPARM_ERROR = 16;
    static private final String CODE_INVALIDPARM_ERROR = "-20116";
    static private final String  MSG_INVALIDPARM_ERROR = "invalid param: `%s`, the param is missing or in incorrect form";

    static public  final Integer IDX_ISEXPUSER = 17;
    static private final String CODE_ISEXPUSER = "-20117";
    static private final String  MSG_ISEXPUSER = "need to be formal user";

    static public  final Integer IDX_MYSQL_ERROR = 18;
    static private final String CODE_MYSQL_ERROR = "-20118";
    static private final String  MSG_MYSQL_ERROR = "Mysql Execute Error";

    static public final List<String> ErrorCode = new ArrayList<String>();
    static public final List<String> ErrorMsg = new ArrayList<String>();

    static {
        ErrorCode.add(CODE_SERVER_ERROR); ErrorMsg.add(MSG_SERVER_ERROR);
        ErrorCode.add(CODE_INCVERIFYCODE_ERROR); ErrorMsg.add(MSG_INCVERIFYCODE_ERROR);
        ErrorCode.add(CODE_INVALIDPHONE_ERROR); ErrorMsg.add(MSG_INVALIDPHONE_ERROR);
        ErrorCode.add(CODE_INVALIDPASSWD_ERROR); ErrorMsg.add(MSG_INVALIDPASSWD_ERROR);
        ErrorCode.add(CODE_DUPLPHONE_ERROR); ErrorMsg.add(MSG_DUPLPHONE_ERROR);
        ErrorCode.add(CODE_USERNOTEXIST_ERROR); ErrorMsg.add(MSG_USERNOTEXIST_ERROR);
        ErrorCode.add(CODE_INCPASSWD_ERROR); ErrorMsg.add(MSG_INCPASSWD_ERROR);
        ErrorCode.add(CODE_METHODNOTALLOWED_ERROR); ErrorMsg.add(MSG_METHODNOTALLOWED_ERROR);
        ErrorCode.add(CODE_INVALIDNAME_ERROR); ErrorMsg.add(MSG_INVALIDNAME_ERROR);
        ErrorCode.add(CODE_INVALIDID_ERROR); ErrorMsg.add(MSG_INVALIDID_ERROR);
        ErrorCode.add(CODE_TOKENEXPIRED_ERROR); ErrorMsg.add(MSG_TOKENEXPIRED_ERROR);
        ErrorCode.add(CODE_FORM_ERROR); ErrorMsg.add(MSG_FORM_ERROR);
        ErrorCode.add(CODE_PATIENTNOTEXIST_ERROR); ErrorMsg.add(MSG_PATIENTNOTEXIST_ERROR);
        ErrorCode.add(CODE_INVALIDJSON_ERROR); ErrorMsg.add(MSG_INVALIDJSON_ERROR);
        ErrorCode.add(CODE_NOPERMISSION_ERROR); ErrorMsg.add(MSG_NOPERMISSION_ERROR);
        ErrorCode.add(CODE_INVALIDTOKEN_ERROR); ErrorMsg.add(MSG_INVALIDTOKEN_ERROR);
        ErrorCode.add(CODE_INVALIDPARM_ERROR); ErrorMsg.add(MSG_INVALIDPARM_ERROR);
        ErrorCode.add(CODE_ISEXPUSER); ErrorMsg.add(MSG_ISEXPUSER);
        ErrorCode.add(CODE_MYSQL_ERROR); ErrorMsg.add(MSG_MYSQL_ERROR);
    }

    static public String GetErrorCode(int idx) { return ErrorCode.get(idx);}

    static public String GetErrorMsg(int idx) { return ErrorMsg.get(idx); }
}
