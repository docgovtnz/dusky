package com.fronde.server.services.snarkimport.reader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SnarkFileReader implements Closeable {

  private final InputStream stream;
  private final int yearOffset;

  public SnarkFileReader(int yearOffset, InputStream stream) {
    if (stream == null) {
      throw new IllegalArgumentException("stream cannot be null");
    }
    this.stream = stream;
    this.yearOffset = yearOffset;
  }

  public List<SnarkFileRecord> readData() throws IOException {
    List<SnarkFileRecord> records = new ArrayList<>();
    SnarkFileRecord record = null;
    do {
      record = readRecord();
      if (record != null) {
        records.add(record);
      }
    } while (record != null);
    stream.close();
    return records;
  }

  private SnarkFileRecord readRecord() throws IOException {
    byte[] nextField = readField("Record Type Field", true);
    if (nextField != null) {
      char recordType = (char) nextField[1];
      switch (recordType) {
        case ('T'):
          return readTimeRecord();
        case ('A'):
          return readArrivalRecord();
        case ('D'):
          return readDepartureRecord();
        case ('W'):
          return readWeightRecord();
        case ('B'):
          return readBatteryRecord();
        case ('F'):
          // lock records are indicated with an F not an L as in the documentation
          return readLockRecord();
        case ('E'):
          return readEggTimerRecord();
        case ('R'):
          throw new RuntimeException("Record type 'R' (RFID) not supported yet");
        default:
          throw new RuntimeException("Unknown record type '" + recordType + "'");
      }
    } else {
      return null;
    }
  }

  private SnarkFileTimeRecord readTimeRecord() throws IOException {
    int dayOfMonth = readBcd("Field 1 (time dayOfMonth)");
    int month = readBcd("Field 2 (time dayOfMonth)");
    int year = yearOffset + readBcd("Field 3 (time dayOfMonth)");
    int hour = readBcd("Field 4 (time dayOfMonth)");
    int minute = readBcd("Field 5 (time dayOfMonth)");
    return new SnarkFileTimeRecord(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
  }

  private SnarkFileArrivalRecord readArrivalRecord() throws IOException {
    int hour = readBcd("Field 1 (arrival hour)");
    int minute = readBcd("Field 2 (arrival minute)");
    int birdID = readUnsignedInt("Field 3 (arrival birdID)");
    skipField("Field 4 (arrival skipped)");
    skipField("Field 5 (arrival skipped)");
    return new SnarkFileArrivalRecord(LocalTime.of(hour, minute), birdID);
  }

  private SnarkFileDepartureRecord readDepartureRecord() throws IOException {
    int hour = readBcd("Field 1 (departure hour)");
    int minute = readBcd("Field 2 (departure minute)");
    int birdID = readUnsignedInt("Field 3 (departure birdID)");
    skipField("Field 4 (arrival skipped)");
    skipField("Field 5 (arrival skipped)");
    return new SnarkFileDepartureRecord(LocalTime.of(hour, minute), birdID);
  }

  private SnarkFileWeightRecord readWeightRecord() throws IOException {
    int hour = readBcd("Field 1 (weight hour)");
    int minute = readBcd("Field 2 (weight minute)");
    int weightHigh = readUnsignedInt("Field 3 (weight weightHigh)");
    int weightLow = readUnsignedInt("Field 4 (weight weightLow)");
    int quality = readUnsignedInt("Field 5 (weight quality)");

    int weight = (weightHigh * 256) + weightLow;

    return new SnarkFileWeightRecord(LocalTime.of(hour, minute), weight, quality);
  }

  private SnarkFileBatteryRecord readBatteryRecord() throws IOException {
    int hour = readBcd("Field 1 (departure hour)");
    int minute = readBcd("Field 2 (departure minute)");
    int type = readUnsignedInt("Field 3 (battery type)");
    skipField("Field 4 (arrival skipped)");
    skipField("Field 5 (arrival skipped)");
    return new SnarkFileBatteryRecord(LocalTime.of(hour, minute), type);
  }

  private SnarkFileLockRecord readLockRecord() throws IOException {
    int hour = readBcd("Field 1 (lock hour)");
    int minute = readBcd("Field 2 (lock minute)");
    int countHigh = readUnsignedInt("Field 3 (lock countHigh)");
    int countLow = readUnsignedInt("Field 4 (lock countLow)");
    skipField("Field 5 (lock skipped)");

    int count = (countHigh * 256) + countLow;

    return new SnarkFileLockRecord(LocalTime.of(hour, minute), count);
  }

  private SnarkFileEggTimerRecord readEggTimerRecord() throws IOException {
    int id = readUnsignedInt("Field 1 (egg timer id)");
    int incubateFlag = readUnsignedInt("Field 2 (departure incubateFlag)");
    int days = readUnsignedInt("Field 3 (departure days)");
    int activityYesterday = readUnsignedInt("Field 4 (departure activityYesterday)");
    int activityLongTerm = readUnsignedInt("Field 5 (departure activityLongTerm)");
    return new SnarkFileEggTimerRecord(id, incubateFlag, days, activityLongTerm, activityYesterday);
  }

  private int readInt(String fieldName) throws IOException {
    byte[] nextField = readField(fieldName);
    return nextField[1];
  }

  private int readUnsignedInt(String fieldName) throws IOException {
    byte[] nextField = readField(fieldName);
    return nextField[1] & 0xFF;
  }

  private int readBcd(String fieldName) throws IOException {
    byte[] nextField = readField(fieldName);
    byte high = (byte) (nextField[1] & 0xf0);
    high >>>= (byte) 4;
    high = (byte) (high & 0x0f);
    byte low = (byte) (nextField[1] & 0x0f);
    int bcd = ((int) high * 10) + (int) low;
    return bcd;
  }

  private void skipField(String fieldName) throws IOException {
    readField(fieldName);
  }

  private byte[] readField(String fieldName) throws IOException {
    return readField(fieldName, false);
  }

  private byte[] readField(String fieldName, boolean allowMissing) throws IOException {
    byte[] nextField = new byte[2];
    int bytesRead = stream.read(nextField, 0, 2);
    if (bytesRead == 2) {
      return nextField;
    } else if (bytesRead == 1) {
      throw new RuntimeException(
          "Expected '" + fieldName + "' but found partial field before end of input");
    } else {
      if (allowMissing) {
        return null;
      } else {
        throw new RuntimeException("Expected '" + fieldName + "' but found end of input");
      }
    }
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
