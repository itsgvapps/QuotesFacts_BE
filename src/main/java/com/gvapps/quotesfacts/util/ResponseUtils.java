package com.gvapps.quotesfacts.util;

import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.dto.response.Error;
import com.gvapps.quotesfacts.dto.response.Result;

public class ResponseUtils {

    public static <T> APIResponse createApiResponse(boolean isSuccess, String statusCode, String statusDesc,
                                                    T data, Error error) {
        return createApiResponse(isSuccess, statusCode, statusDesc, data, null, null, error);
    }

    public static <T> APIResponse createApiResponse(boolean isSuccess, String statusCode, String statusDesc,
                                                    T data, String title, String subTitle, Error error) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setSuccess(isSuccess);
        apiResponse.setError(error);
        Result<T> result = new Result<>();
        result.setTitle(title);
        result.setSubTitle(subTitle);
        result.setData(data);
        result.setStatusCode(statusCode);
        result.setStatusDesc(statusDesc);
        apiResponse.setResult(result);
        return apiResponse;
    }

    // ✅ Convenience method for success responses
    public static <T> APIResponse success(String code, String desc, T data) {
        return createApiResponse(true, code, desc, data, null);
    }

    public static <T> APIResponse success(String code, String desc, T data, String title, String subTitle) {
        return createApiResponse(true, code, desc, data, title, subTitle, null);
    }

    // ✅ Convenience method for error responses
    public static APIResponse error(String code, String desc, String message) {
        return createApiResponse(false, code, desc, null, new Error(code, message));
    }
}
