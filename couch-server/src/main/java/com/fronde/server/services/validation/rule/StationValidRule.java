package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StationValidRule extends AbstractRule {

  private static final List<String> snarkTypeList =
      Arrays.asList("Hub", "Berrol", "Errol", "Nest Snark", "Feed-out Snark");
  private static final String REGEX = "^([0-9]|[1-9][0-9]|[1-9][0-9][0-9])$";
  private static final Pattern pattern = Pattern.compile(REGEX);

  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {

    // if station is null, let it be caught by the required rule for station
    if (propertyValue != null) {
      String station = String.valueOf(propertyValue);

      // check first part of station is a valid station/snark type
      String[] stationArr = station.split("\\d+", 2);

      String type = stationArr[0].trim();
      if (!snarkTypeList.contains(type)) {
        resultFactory.addResult("StationTypeRule");
      }

      // check if there was actually a space and/or number after the station type
      if (station.equals(type)) {
        resultFactory.addResult("StationFormatRule");
      } else {
        // check characters after type are Integers in range 0 ~ 255
        String device = station.substring(type.length() + 1).trim();
        if (!isInRange(device) || !isInteger(device) || (Integer.parseInt(device) > 255)) {
          resultFactory.addResult("StationFormatRule");
        } else {
          // check for space between station type and device number
          String buildStation = type + " " + device;
          if (!station.equals(buildStation)) {
            resultFactory.addResult("StationFormatRule");
          }
        }
      }
    }

  }

  private boolean isInRange(String device) {
    Matcher matcher = pattern.matcher(device);
    return matcher.matches();
  }

  public static boolean isInteger(String string) {
    if (string == null) {
      return false;
    }
    try {
      Integer i = Integer.parseInt(string);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}


