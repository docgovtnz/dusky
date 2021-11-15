package com.fronde.dusky;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {

  private static byte[] salt = "<SALT>";

  private static String password;

  /**
   * Encrypt with AES.
   *
   * @param value The value to encrypt.
   * @return The encrypted value in BASE64.
   */
  public static String encrypt(String value) {
    try {
      SecureRandom sr = new SecureRandom();
      byte[] initVector = new byte[16];
      sr.nextBytes(initVector);
      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec skeySpec = generateKey();

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

      byte[] encrypted = cipher.doFinal(value.getBytes());

      byte[] val = new byte[initVector.length + encrypted.length];
      System.arraycopy(initVector, 0, val, 0, 16);
      System.arraycopy(encrypted, 0, val, 16, encrypted.length);

      return Base64.getEncoder().encodeToString(val);
    } catch (Exception ex) {
      throw new RuntimeException("Encryption error", ex);
    }
  }

  /**
   * Decrypt a value
   *
   * @param value The encrypted string (in BASE64).
   * @return The decrypted vaule.
   */
  public static String decrypt(String value) {
    try {
      byte[] valueAsBytes = Base64.getDecoder().decode(value);

      // IV is the first 16 bytes.
      IvParameterSpec iv = new IvParameterSpec(valueAsBytes, 0, 16);
      SecretKeySpec skeySpec = generateKey();

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

      byte[] decrypted = cipher.doFinal(valueAsBytes, 16, valueAsBytes.length - 16);
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new RuntimeException("Encryption error", ex);
    }
  }

  public static SecretKeySpec generateKey()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Use PBKDF2 to generate a key based on the password.
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
    SecretKey tmp = factory.generateSecret(spec);
    return new SecretKeySpec(tmp.getEncoded(), "AES");
  }

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    System.out.println("Encryption Tool");
    System.out.println("---------------");
    System.out.print("Enter encryption key: ");

    password = sc.nextLine();

    System.out.println("E - Encrypt");
    System.out.println("D - Decrypt");
    System.out.println("Q - Decrypt");

    String in = null;
    do {
      System.out.println();
      System.out.print("Choose (E|D|Q): ");
      in = sc.nextLine();

      switch (in) {
        case "E":
          System.out.print("Plain Text: ");
          String input = sc.nextLine();
          System.out.println("Encrypted: " + encrypt(input));
          break;

        case "D":
          System.out.print("Plain Text: ");
          String input2 = sc.nextLine();
          System.out.println("Decrypted: " + decrypt(input2));
          break;

        case "Q":
          break;

        default:
          System.out.println("That's not a valid option");
      }
    } while (!"Q".equals(in));

  }
}
