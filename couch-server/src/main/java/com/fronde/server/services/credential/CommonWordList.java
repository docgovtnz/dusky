package com.fronde.server.services.credential;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "common-words")
public class CommonWordList {

  @XmlElement(name = "word")
  private List<String> words;

  public List<String> getWords() {
    return this.words;
  }

}
