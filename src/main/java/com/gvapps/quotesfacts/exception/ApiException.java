package com.gvapps.quotesfacts.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String code;
    private final String description;

    public ApiException(String code, String description, String message) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
        this.description = "Business Error";
    }
}
