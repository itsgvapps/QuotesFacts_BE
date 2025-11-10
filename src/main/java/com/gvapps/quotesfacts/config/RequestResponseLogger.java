package com.gvapps.quotesfacts.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@WebFilter("/*")
public class RequestResponseLogger extends OncePerRequestFilter {

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[90m";

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private static final Set<String> IGNORED_HEADERS = new HashSet<>(Arrays.asList(
            "accept", "accept-encoding", "accept-language", "connection", "content-length",
            "host", "user-agent", "cache-control", "postman-token", "upgrade-insecure-requests"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(req, res);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logFormatted(req, res, duration);
            res.copyBodyToResponse();
        }
    }

    private void logFormatted(ContentCachingRequestWrapper req,
                              ContentCachingResponseWrapper res,
                              long duration) {

        String method = req.getMethod();
        String uri = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        int status = res.getStatus();
        String color = status >= 500 ? RED : status >= 400 ? YELLOW : GREEN;

        String reqBody = new String(req.getContentAsByteArray(), StandardCharsets.UTF_8).trim();
        String resBody = new String(res.getContentAsByteArray(), StandardCharsets.UTF_8).trim();

        String headers = Collections.list(req.getHeaderNames()).stream()
                .filter(h -> !IGNORED_HEADERS.contains(h.toLowerCase()))
                .map(h -> h + "=" + req.getHeader(h))
                .collect(Collectors.joining(", ", "{", "}"));

        boolean hasReqBody = !"GET".equalsIgnoreCase(method) && !reqBody.isEmpty();

        // ✅ Add formatted date-time (colored cyan)
        String timestamp = CYAN + OffsetDateTime.now(ZoneId.systemDefault()).format(ISO_FORMATTER) + RESET;

        System.out.printf(
                "%n%s═══════════════ [REQUEST @ %s] ═══════════════%s%n" +
                        "%s→ %s %s | Headers: %s%s%s%s%s%n" +
                        (hasReqBody ? "%sBody:%s %s%n" : ""),
                BLUE, timestamp, RESET,
                CYAN, method, uri, GRAY, truncate(headers), RESET, CYAN, RESET,
                hasReqBody ? GRAY : "", hasReqBody ? RESET : "", hasReqBody ? truncate(reqBody) : ""
        );

        System.out.printf(
                "%s─────────────── [RESPONSE] ───────────────%s%n" +
                        "%sStatus:%s %s%d%s | %sTime:%s %dms%n" +
                        "%sBody:%s %s%n" +
                        "%s═══════════════════════════════════════%s%n",
                BLUE, RESET,
                CYAN, RESET, color, status, RESET, CYAN, RESET, duration,
                GRAY, RESET, truncate(resBody), BLUE, RESET
        );
    }

    private String truncate(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.length() > 400 ? text.substring(0, 400) + "..." : text;
    }
}
