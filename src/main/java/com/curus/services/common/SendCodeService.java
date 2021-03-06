package com.curus.services.common;

import com.curus.httpio.request.common.SendCodeRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.common.SendCodeResponseData;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.CateValidate;
import com.curus.utils.validate.PhoneValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class SendCodeService {

    private static Log logger = LogFactory.getLog(SendCodeService.class);
    private SendCodeRequest request;
    private SendCodeResponseData responseData;
    private ErrorData errorData;

    public SendCodeService(SendCodeRequest request) {
        this.request = request;
        this.responseData = null;
        this.errorData = null;
    }

    private ErrorData validation() {
        if ( (errorData = PhoneValidate.validatePhoneNumber(request.getPhone())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        } else if ( (errorData = CateValidate.validateCateAndToken(request.getCate(),request.getToken())) != null) {
            logger.warn(LogUtils.Msg(errorData,request));
        }
        return errorData;
    }

    public static String getCodeAndSave2Cache(String cate, String token, String phone, String id_number) {
        String code = CacheUtils.getCode();
        CacheUtils.putCode2Cache(cate, token, phone, code);
        return code;
    }

    private ErrorData sendCode() {
        responseData = new SendCodeResponseData(getCodeAndSave2Cache(
                request.getCate(),
                request.getToken(),
                request.getPhone(),
                request.getId_number()));
        return errorData;
    }

    public ResponseBase process() {
        if (validation() == null && sendCode() == null ) {
            logger.info(LogUtils.Msg("Success SendCode",request,responseData));
            return new ResponseBase(StatusConst.OK,responseData);
        } else {
            return new ResponseBase(StatusConst.ERROR, errorData);
        }
    }
}
