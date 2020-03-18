package com.sysco.payplus.dto;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
public class ErrorDTO {

    public static final String ERROR_CODE_VALIDATION_FAILURE = "1000";
    public static final String ERROR_CODE_INTERNAL_ERROR = "999";


    private static Map<String, String> errorCodes = new HashMap<>();
    static {
        errorCodes.put(ERROR_CODE_VALIDATION_FAILURE,"Validation failure");
        errorCodes.put(ERROR_CODE_INTERNAL_ERROR,"Internal service failure");
    }


    private String code;
    private String message;
    private Object data;

    public ErrorDTO(String code) {
        this.code = code;
        message = ErrorDTO.errorCodes.get(code);
        data="Service team has been informed of the internal service failure";
    }
    public ErrorDTO(String code, Object data) {
        this.code = code;
        message = ErrorDTO.errorCodes.get(code);
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
