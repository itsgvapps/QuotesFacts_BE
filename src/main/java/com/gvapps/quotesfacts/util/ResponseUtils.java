package com.gvapps.quotesfacts.util;

import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.dto.response.Error;
import com.gvapps.quotesfacts.dto.response.Result;

public class ResponseUtils {

    public static <T> APIResponse createApiResponse(boolean isSuccess, String statusCode, String statusDesc,
                                                    T data, Error error) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setSuccess(isSuccess);
        apiResponse.setError(error);
        Result result = new Result();
        result.setData(data);

        result.setStatusCode(statusCode);
        result.setStatusDesc(statusDesc);
        apiResponse.setResult(result);
        return apiResponse;
    }
}
