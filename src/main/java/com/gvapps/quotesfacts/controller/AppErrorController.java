package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.util.ResponseUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<APIResponse> handleError(HttpServletRequest request) {
        Object statusAttribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusAttribute instanceof Integer statusCode
                ? statusCode
                : HttpStatus.INTERNAL_SERVER_ERROR.value();

        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (message == null) {
            message = HttpStatus.valueOf(status).getReasonPhrase();
        }

        if (error == null) {
            error = "An error occurred";
        }

        log.error("[AppErrorController] Error occurred - Status: {}, Message: {}, Error: {}", status, message, error);

        APIResponse response = ResponseUtils.error(
                String.valueOf(status),
                HttpStatus.valueOf(status).getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}

