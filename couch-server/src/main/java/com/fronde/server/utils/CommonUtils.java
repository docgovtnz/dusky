package com.fronde.server.utils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

  public Date nullSafeGetDate(String dateStrInISO) {
    // parse the date as an ISO instance
    if (dateStrInISO != null) {
      return Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateStrInISO)));
    } else {
      return null;
    }
  }

  public static Date getExpectedExpiryDate(Long lifeExpectancy, Date dateTime) {
    if (lifeExpectancy != null && dateTime != null) {
      int totalDays = lifeExpectancy.intValue() * 7;
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateTime);
      calendar.add(Calendar.DAY_OF_YEAR, totalDays);
      return calendar.getTime();
    }
    return null;
  }

  public static Float nullSafeGetFloat(Number number) {
    return number == null ? null : number.floatValue();
  }
}
