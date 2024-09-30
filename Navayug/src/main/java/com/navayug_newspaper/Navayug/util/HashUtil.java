package com.navayug_newspaper.Navayug.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

  public static String hashApiKey(String apiKey) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(apiKey.getBytes());
      // Convert byte array into a Base64 encoded string (optional for readability)
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing API Key", e);
    }
  }

  public static void main(String[] args) {
    String apiKey = "X0IFapgvwGA2mA66Ybd2DRLdYQt5VeKG";
    String hashedKey = hashApiKey(apiKey);
    System.out.println("Hashed API Key: " + hashedKey);
  }
}

