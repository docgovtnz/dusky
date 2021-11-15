package com.fronde.server.services.common;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ObjectIdFactory {

  public String create() {
    return UUID.randomUUID().toString();
  }

  static String uuidToBase64(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    String b64Str = Base64.getUrlEncoder().encodeToString(bb.array());
    if (b64Str.endsWith("==")) {
      b64Str = b64Str.substring(0, b64Str.length() - "==".length());
    }
    return b64Str;
  }

  static UUID uuidFromBase64(String str) {
    byte[] bytes = Base64.getUrlDecoder().decode(str);
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    UUID uuid = new UUID(bb.getLong(), bb.getLong());
    return uuid;
  }

}
