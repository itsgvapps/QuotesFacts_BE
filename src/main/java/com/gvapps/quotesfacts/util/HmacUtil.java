package com.gvapps.quotesfacts.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {
    private static final String SECRET = "gvappsgvapps1234";

    public static String hmac(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec sk = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        mac.init(sk);
        byte[] raw = mac.doFinal(data.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
