package com.fronde.server.services.record;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.CheckmateDataEntity;
import com.fronde.server.services.bird.BirdService;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CheckmateHelper {

  public static String deriveBird(BirdService birdService, String island, int channel) {
    BirdEntity result = birdService.findBirdIDByTransmitter(island, channel, "Female");
    return result == null ? null : result.getId();
  }

  public static Date deriveTime(Date recordTime, Integer hoursAgo) {
    if (hoursAgo == null || hoursAgo == 255) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(recordTime);
    cal.add(Calendar.HOUR, -1 * hoursAgo);
    return cal.getTime();
  }

  public static boolean areIdentical(CheckmateDataEntity newItem, CheckmateDataEntity oldItem) {
    return Objects.equals(newItem.getFemaleTx1(), oldItem.getFemaleTx1())
        && Objects.equals(newItem.getFemaleTx2(), oldItem.getFemaleTx2())
        && Objects.equals(newItem.getFemaleTx(), oldItem.getFemaleTx())
        && Objects.equals(newItem.getTime1(), oldItem.getTime1())
        && Objects.equals(newItem.getTime2(), oldItem.getTime2())
        && Objects.equals(newItem.getTime(), oldItem.getTime())
        && Objects.equals(newItem.getDuration1(), oldItem.getDuration1())
        && Objects.equals(newItem.getDuration2(), oldItem.getDuration2())
        && Objects.equals(newItem.getDuration(), oldItem.getDuration())
        && Objects.equals(newItem.getQuality1(), oldItem.getQuality1())
        && Objects.equals(newItem.getQuality2(), oldItem.getQuality2())
        && Objects.equals(newItem.getQuality(), oldItem.getQuality())
        && Objects.equals(newItem.getBirdId(), oldItem.getBirdId());
  }
}
