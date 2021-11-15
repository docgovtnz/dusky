package com.fronde.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

public class EnvironmentProcessor implements EnvironmentPostProcessor {

  private static final Logger logger = LoggerFactory.getLogger(EnvironmentPostProcessor.class);

  public static final String GENERATED_PROPERTIES_FILE = "./generated.properties";
  public static final String REPLICATION_PROPERTIES_FILE = "./replication.properties";

  private static final String password = "<ENCRYPTION PASSWORD>";
  private static final byte[] salt = "<SALT>";

  Pattern PATTERN_ENCRYPTED_PROPERTY = Pattern.compile("encrypted\\((.*?)\\)");

  private void loadPropertiesFile(ConfigurableEnvironment environment, String filename) {
    File sourceFile = new File(System.getProperty("user.dir"), filename);
    try {
      Properties properties = new Properties();
      properties.load(new FileReader(sourceFile));
      environment.getPropertySources().addFirst(new PropertiesPropertySource(filename, properties));
      logger.info("Loaded properties file ok: " + sourceFile.getAbsolutePath());
    } catch (IOException ex) {
      // Can't use logger, this code is executed too early for logger to work properly
      logger.warn("Could not load properties: " + sourceFile.getAbsolutePath());
    }
  }

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    // Add additional and machine specific properties files.
    loadPropertiesFile(environment, GENERATED_PROPERTIES_FILE);
    loadPropertiesFile(environment, REPLICATION_PROPERTIES_FILE);

    // Decrypt properties.
    for (Iterator<PropertySource<?>> it = environment.getPropertySources().iterator();
        it.hasNext(); ) {
      PropertySource<?> ps = it.next();
      if (ps.getSource() instanceof Map) {
        Map<Object, Object> map = (Map<Object, Object>) ps.getSource();

        map.keySet().forEach(key -> {
          Object value = map.get(key);
          if (value instanceof String) {
            String val = (String) value;
            Matcher m = PATTERN_ENCRYPTED_PROPERTY.matcher(val);
            if (m.matches()) {
              String decryptedValue = decrypt(m.group(1));
              map.put(key, decryptedValue);
            }
          } else if (value instanceof OriginTrackedValue && ((OriginTrackedValue) value)
              .getValue() instanceof String) {
            String val = (String) ((OriginTrackedValue) value).getValue();
            Matcher m = PATTERN_ENCRYPTED_PROPERTY.matcher(val);
            if (m.matches()) {
              String decryptedValue = decrypt(m.group(1));
              OriginTrackedValue newValue = OriginTrackedValue
                  .of(decryptedValue, ((OriginTrackedValue) value).getOrigin());
              map.put(key, newValue);
            }
          }
        });
      }
    }
  }

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
      logger.error("Could not decrypt value: " + value);
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

}

