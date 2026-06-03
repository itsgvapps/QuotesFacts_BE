package com.gvapps.quotesfacts.config;

import com.gvapps.quotesfacts.util.HmacUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class HmacFilter extends OncePerRequestFilter {

    private static final String APP_ID = "myapp";

    @Value("${security.hmac.enabled:true}")
    private boolean hmacEnabled;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain)
            throws IOException, ServletException {

        // Keep HMAC enabled by default, but allow the dev profile to disable it for Postman/local testing.
        if (!hmacEnabled) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String appId = req.getHeader("x-app-id");
            String timestamp = req.getHeader("x-timestamp");
            String nonce = req.getHeader("x-nonce");
            String signature = req.getHeader("x-signature");

            /*Client:
            Sends headers: X-APP-ID, X-TIMESTAMP, X-NONCE, X-SIGNATURE
            Signature = HMAC_SHA256(timestamp + nonce, SECRET)*/

            /*if (!APP_ID.equals(appId)) {
                res.sendError(401, "Invalid App ID");
                log.info("[HmacFilter] >> [doFilterInternal] >> Invalid App ID");
                return;
            }*/

            if (timestamp == null || nonce == null || signature == null) {
                res.sendError(401, "Missing headers");
                log.error("[HmacFilter] >> [doFilterInternal] >> Missing headers: timestamp: {}; nonce:{}; signature:{}", timestamp, nonce, signature);
                return;
            }

            // Replay protection
            /*long ts = Long.parseLong(timestamp.trim());
            final long ALLOWED_DRIFT_MS = 15 * 60 * 1000; // 15 minutes
            long now = System.currentTimeMillis();
            long diff = Math.abs(now - ts);
            if (diff > ALLOWED_DRIFT_MS) {
                log.error("[HmacFilter] >> [doFilterInternal] >> Expired request. Now={}, ts={}, diff={}", now, ts, diff);
                res.sendError(401, "Expired request");
                return;
            }*/

            // Compute expected signature
            String dataToSign = timestamp + nonce;
            String expected = HmacUtil.hmac(dataToSign);
            if (!expected.equals(signature)) {
                res.sendError(401, "Invalid signature");
                log.error("[HmacFilter] >> [doFilterInternal] >> Invalid signature");
                return;
            }

            chain.doFilter(req, res);

        } catch (Exception e) {
            res.sendError(401, "Unauthorized");
            log.error("[HmacFilter] >> [doFilterInternal] >> Unauthorized");
        }
    }
}
