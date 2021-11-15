package com.fronde.server.services.credential;

import com.fronde.server.utils.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

  private static List<String> words;

  private final SecureRandom secureRandom = new SecureRandom();

  public String generateRandomPassword() {
    StringBuilder bob = new StringBuilder();
    for (int i = 1; i <= 2; i++) {
      String nextWord = getWords().get(secureRandom.nextInt(getWords().size()));
      Matcher m = Pattern.compile("^(.{1})(.*)$").matcher(nextWord);
      m.find();
      nextWord = m.group(1).toUpperCase() + m.group(2).toLowerCase();
      bob.append(nextWord);
    }
    bob.append(secureRandom.nextInt(89) + 10);
    return bob.toString();
  }

  private static List<String> getWords() {
    if (words == null) {
      String resourceName = "/common-words.xml";
      InputStream resourceAsStream = PasswordGenerator.class.getResourceAsStream(resourceName);
      String xml = null;
      try {
        xml = IOUtils.toString(resourceAsStream);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      CommonWordList list = XmlUtils.convertFromString(CommonWordList.class, xml);
      words = list.getWords();
    }
    return words;
  }

}
