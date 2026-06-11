package com.gvapps.quotesfacts.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private String title;
    private String subTitle;
    private T data;
    private String statusCode;
    private String statusDesc;
}
