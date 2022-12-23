package com.nemo.jwt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Token {
    private static final Gson GSON = new Gson();
    private static final Type HASHMAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();
    private static final String ALGORITHM = "HmacSHA256";
    private static final String SECRET = "123456";
    private Map<String, String> headers;
    private Map<String, String> payload;
    private String signature;

    public Token() {
        this.headers = new HashMap<>();
        this.headers.put("alg", "HS256");
        this.headers.put("typ", "JWT");
        this.payload = new HashMap<>();
    }

    public void addPayload(String key, String value) {
        payload.put(key, value);
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static String base64Encode(String str) {
        return base64Encode(str.getBytes());
    }

    private static String base64Decode(String str) {
        return new String(
                Base64.getUrlDecoder().decode(str.getBytes(StandardCharsets.UTF_8))
        );
    }

    public String encode() {
        String encodedHeader = base64Encode(GSON.toJson(headers));
        String encodedPayload = base64Encode(GSON.toJson(payload));
        signature = hmacWithJava(ALGORITHM, encodedHeader+"."+encodedPayload, SECRET);
        return String.format("%s.%s.%s",
                encodedHeader,
                encodedPayload,
                signature);
    }

    @Override
    public String toString() {
        return "Token{" +
                "headers=" + headers +
                ", payload=" + payload +
                ", signature='" + signature + '\'' +
                '}';
    }

    public static Token decode(String encodedToken) {
        String[] split = encodedToken.split("\\.");
        if (split.length != 3) {
            throw new IllegalArgumentException(String.format("Invalid JWT: %s", encodedToken));
        }
        String signature = hmacWithJava(ALGORITHM, split[0]+"."+split[1], SECRET);
        if (!signature.equals(split[2])) {
            throw new IllegalArgumentException(String.format("Invalid JWT signature: %s. expecting: %s", split[2], signature));
        }
        Token token = new Token();
        token.headers = GSON.fromJson(base64Decode(split[0]), HASHMAP_TYPE);
        token.payload = GSON.fromJson(base64Decode(split[1]), HASHMAP_TYPE);
        token.signature = split[2];
        return token;
    }

    public static String hmacWithJava(String algorithm, String data, String key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return base64Encode(mac.doFinal(data.getBytes()));
    }

/*    public static String byteToHex(byte[] byteArray) {
        String hex = "";
        // Iterating through each byte in the array
        for (byte i : byteArray) {
            hex += String.format("%02X", i);
        }
        return  hex;
    }*/
}
