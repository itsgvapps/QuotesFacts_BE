package com.gvapps.quotesfacts.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse {
    private boolean success;
    private Error error;
    private Result result;
}
