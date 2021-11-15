package com.fronde.server.services.common;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.junit.Test;

public class ObjectIdFactoryTests {

  @Test
  public void testEncodeEqualsDecode() throws Exception {
    UUID uuidInitial = UUID.randomUUID();
    String b64 = ObjectIdFactory.uuidToBase64(uuidInitial);
    UUID uuidDecoded = ObjectIdFactory.uuidFromBase64(b64);

    assertEquals(uuidInitial, uuidDecoded);
  }
}
