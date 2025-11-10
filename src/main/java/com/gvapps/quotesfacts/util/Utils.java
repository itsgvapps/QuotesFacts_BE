package com.gvapps.quotesfacts.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

@Slf4j
public class Utils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Utils() {
    }

    /**
     * Safely converts an object to a JSON string.
     * ------------------------------------------------------------
     * ✅ Returns "{}" for empty objects
     * ✅ Returns "null" for null input
     * ✅ Handles serialization errors gracefully
     */
    public static String getJsonString(Object obj) {
        if (obj == null) {
            log.warn("[Utils] >> [getJsonString] Object is null, returning \"null\"");
            return "null";
        }

        try {
            String json = mapper.writeValueAsString(obj);
            // If JSON is empty or "{}" (no properties), return a safe placeholder
            if (StringUtils.isBlank(json) || "{}".equals(json)) {
                log.debug("[Utils] >> [getJsonString] Empty JSON object for type: {}", obj.getClass().getSimpleName());
                return "{}";
            }
            return json;
        } catch (JsonProcessingException e) {
            log.error("[Utils] >> [getJsonString] Failed to convert to JSON ({}): {}", obj.getClass().getSimpleName(), e.getMessage());
            return "{\"error\":\"JSON serialization failed\"}";
        } catch (Exception e) {
            log.error("[Utils] >> [getJsonString] Unexpected error during JSON conversion: {}", e.getMessage(), e);
            return "{\"error\":\"Unexpected serialization error\"}";
        }
    }

    private Integer getRandomNumberInts(int min, int max) {
        return new Random().ints(min, max + 1).findFirst().getAsInt();
    }

    /**
     * Read all the content from a file.
     *
     * @param fileName
     * @param filePath
     * @return String.
     * @throws IOException
     */
    public String readFile(String filePath, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = null;
        File file2 = new File(filePath, fileName);
        fileReader = new FileReader(file2);
        try (BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        fileReader.close();
        return sb.toString();
    }

}
