package com.fronde.server.config;

import com.fronde.server.domain.SettingEntity;
import com.fronde.server.services.setting.SettingService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingStartup {

  public static Integer juvenileThresholdDays = 150;

  public static Double adultThresholdYears = 4.5;

  static Map<String, Object[]> initialSettings;

  static {
    initialSettings = new HashMap<>();

    initialSettings.put("AGE_CLASS_JUVENILE_THRESHOLD_DAYS",
        new Object[]{"Age Class Juvenile Threshold (Days)", juvenileThresholdDays, null});
    initialSettings.put("AGE_CLASS_ADULT_THRESHOLD_YEARS",
        new Object[]{"Age Class Adult Threshold (Years)", adultThresholdYears, null});
    initialSettings.put("MATING_QUALITY_MEDIUM_THRESHOLD",
        new Object[]{"Mating Quality Medium Threshold", 40, null});
    initialSettings.put("MATING_QUALITY_HIGH_THRESHOLD",
        new Object[]{"Mating Quality High Threshold", 70, null});
    initialSettings.put("HELP", new Object[]{"Help Link",
        "https://paper.dropbox.com/doc/Dusky-Database-Manual-azkbFBEjt7DPcGnIR0KYq", null});
  }

  @Autowired
  protected SettingService service;

  void createSettings() {
    for (String key : initialSettings.keySet()) {
      Object[] parameters = initialSettings.get(key);
      if (service.findById(key) == null) {
        SettingEntity s = new SettingEntity();
        s.setId(key);
        s.setName((String) parameters[0]);
        s.setValue(parameters[1]);
        s.setType((String) parameters[2]);
        service.saveWithThrow(s);
      }
    }
  }

}
