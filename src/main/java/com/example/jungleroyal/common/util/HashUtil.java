package com.example.jungleroyal.common.util;

import org.springframework.beans.factory.annotation.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class HashUtil {

    @Value("${secret.hash.key}")
    private static String hashKey;

    // SHA-256 해시 메서드
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            String base64Hash = Base64.getEncoder().encodeToString(hash);
            return base64Hash.replace("/", "_");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 Algorithm not found", e);
        }
    }

    // UUID와 해시를 조합한 암호화 메서드
    public static String encryptWithUUIDAndHash() {
        // UUID 생성
        String uuid = UUID.randomUUID().toString();

        // UUID와 입력 데이터를 결합
        String combinedData = uuid + hashKey;

        // 해싱 처리
        return hash(combinedData);
    }
}
