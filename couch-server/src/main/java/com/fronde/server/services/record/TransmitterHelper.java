package com.fronde.server.services.record;

public class TransmitterHelper {

  public static Integer deriveValue(Integer val1, Integer val2) {
    if (val1 == null || val1 < 2 || val2 == null || val1 < 2) {
      return null;
    } else {
      return (val1 - 2) * 10 + val2 - 2;
    }
  }
}
