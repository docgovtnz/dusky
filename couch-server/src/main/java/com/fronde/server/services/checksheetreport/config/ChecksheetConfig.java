package com.fronde.server.services.checksheetreport.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.util.StringUtils;


/**
 * Checksheet configuration.
 * <p>
 * Can be generated from the database in pieces - however, need to check for XML validity
 * (ampersand, less than symbols, etc.).
 * <p>
 * select distinct concat('<mapping recordType="', RecoveryType, '"', case when reason is not null
 * then concat(' reason="', Reason, '"') end, ' checksheetSymbol="', checksheetSymbol, '"/>') from
 * [dbo].[RecordMetadata] WHERE  CheckSheetSymbol IS NOT NULL;
 * <p>
 * select distinct concat('<entry><key>', CheckSheetSymbol,'</key><value>',
 * CheckSheetQuality,'</value></entry>') from RecordMetadata where CheckSheetSymbol is not null
 */

@XmlRootElement(name = "ChecksheetConfig")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ChecksheetConfig {

  @XmlElement(name = "mapping")
  @XmlElementWrapper(name = "mappings")
  private List<ChecksheetElement> symbolMappings = new LinkedList<>();

  @XmlElement
  @XmlElementWrapper(name = "symbolPriorities")
  private final Map<String, Integer> checksheetPriority = new HashMap<>();


  private transient Map<String, ChecksheetElement> mappedConfig = null;

  public List<ChecksheetElement> getSymbolMappings() {
    return symbolMappings;
  }

  public void setSymbolMappings(List<ChecksheetElement> symbolMappings) {
    this.symbolMappings = symbolMappings;
  }

  public ChecksheetElement get(String recordType, String reason) {
    return getMappedConfig().get(getKey(recordType, reason));
  }

  private synchronized Map<String, ChecksheetElement> getMappedConfig() {
    if (mappedConfig == null) {
      mappedConfig = new HashMap<>();
      for (ChecksheetElement el : this.symbolMappings) {
        mappedConfig.put(getKey(el.getRecordType(), el.getReason()), el);
      }
    }
    return mappedConfig;
  }

  private String getKey(String recordType, String reason) {
    return recordType + "/" + (StringUtils.isEmpty(reason) ? "" : reason);
  }

  public Map<String, Integer> getChecksheetPriority() {
    return this.checksheetPriority;
  }

}
