package com.curus.services.common;

import com.curus.dao.PatientDao;
import com.curus.httpio.request.common.SendCodeRequest;
import com.curus.httpio.response.ErrorData;
import com.curus.httpio.response.ResponseBase;
import com.curus.httpio.response.common.SendCodeResponseData;
import com.curus.model.Account;
import com.curus.model.Patient;
import com.curus.utils.CacheUtils;
import com.curus.utils.LogUtils;
import com.curus.utils.SpringContextUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CateConst;
import com.curus.utils.constant.StatusConst;
import com.curus.utils.validate.CateValidate;
import com.curus.utils.validate.PhoneValidate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * Created by stupid-coder on 23/1/16.
 */
public class SendCodeService {

    private static Log logger = LogFactory.getLog(SendCodeService.class);
    private SendCodeRequest request;
    private SendCodeResponseData responseData;
    private PatientDao driver;
    private ErrorData errorData;

    public SendCodeService(SendCodeRequest request) {
        this.request = request;
        this.responseData = null;
        driver = (PatientDao) SpringContextUtils.getBean("patientDao");
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

    public static String getCodeAndSave2Cache(PatientDao driver,String cate, String token, String phone, String id_number) {
        String code = CacheUtils.getCode();
        switch (CateValidate.getIdx(cate)) {
            case CateConst.IREGISTER:
            case CateConst.IMODIFY_PHONE:
            case CateConst.IFORGET_PASSWD:
                CacheUtils.putCode2Cache(cate, token, phone, code);
                logger.info(LogUtils.Msg("Get Code",String.format("cate:%s,code:%s,phone:%s,token:%s",cate,code,phone,token)));
                break;
            case CateConst.IADD_PATIENT:
                Patient patient = driver.select(TypeUtils.getWhereHashMap("id_number",id_number));
                if ( patient != null ) { phone = patient.getPhone(); }
                CacheUtils.putCode2Cache(cate,token,phone,code);
                //Account account = (Account) CacheUtils.getObject4Cache(token); message push
                break;
        }
        return code;
    }

    private ErrorData sendCode() {
        responseData = new SendCodeResponseData(getCodeAndSave2Cache(driver,
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
