package com.example.taskmanagement.util;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        String secret = Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
        System.out.println("Generated JWT Secret:");
        System.out.println(secret);
    }
}
