package com.fronde.server;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

  private static final String _NUMBERS = "0123456789";
  private static final String _UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String _LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String _ALL = _NUMBERS + _UPPER + _LOWER;

  private static final char[] NUMBERS = _NUMBERS.toCharArray();
  private static final char[] UPPER = _UPPER.toCharArray();
  private static final char[] LOWER = _LOWER.toCharArray();
  private static final char[] ALL = _ALL.toCharArray();

  private final SecureRandom random;

  public PasswordGenerator() {
    random = new SecureRandom();
  }

  public String generatePassword() {
    ArrayList<Character> chars = new ArrayList<>(16);

    of(chars, NUMBERS, 2);
    of(chars, UPPER, 1);
    of(chars, LOWER, 1);
    of(chars, ALL, 12);

    Collections.shuffle(chars, random);

    StringBuilder builder = new StringBuilder();
    chars.stream().forEach(c -> builder.append(c));
    return builder.toString();
  }

  public void of(List<Character> target, char[] source, int num) {
    for (int i = 0; i < num; i++) {
      target.add(source[random.nextInt(source.length)]);
    }
  }


}
