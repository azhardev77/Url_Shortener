package com.azhar.urlshortener.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HashGenerator {
    
    private static final int SHORT_CODE_LENGTH = 7;
    private static final String ALGORITHM = "SHA-256";
    
    public String generateShortCode(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            
            // Convert hash to Base64 and take first SHORT_CODE_LENGTH characters
            String base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return base64Hash.substring(0, SHORT_CODE_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to random string if hashing fails
            return generateRandomString();
        }
    }
    
    public String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(SHORT_CODE_LENGTH);
    }
    
    public String generateCustomShortCode(String customAlias) {
        return customAlias.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}