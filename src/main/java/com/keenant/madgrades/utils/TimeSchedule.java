package com.keenant.madgrades.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TimeSchedule {
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

  private final int startTime;
  private final int endTime;

  private TimeSchedule(int startTime, int endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * Parse a time schedule string, i.e.: 14:00-14:50 or 07:30-08:20
   * @param timeStr the time string
   * @return the parsed time schedule
   * @throws ParseException if the input is not parsable
   */
  public static TimeSchedule parse(String timeStr) {
    Calendar calendar = Calendar.getInstance();
    int startTime;
    int endTime;

    String[] timeSplit = null;

    if (timeStr.contains(" - ")) {
      timeSplit = timeStr.split(" - ");
    } else if (timeStr.contains(" ")) {
      timeSplit = timeStr.split(" ");
    }

    if (timeSplit == null ||
        timeSplit.length != 2 ||
        timeSplit[0].length() < 4 ||
        timeSplit[1].length() < 4) {
      return new TimeSchedule(-1, -1);
    }

    String startTimeStr = timeSplit[0];
    String endTimeStr = timeSplit[1];

    Date startDate;
    try {
      startDate = TIME_FORMAT.parse(startTimeStr);
    } catch (ParseException e) {
      throw new IllegalArgumentException(timeStr, e);
    }
    calendar.setTime(startDate);
    startTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

    Date endDate;
    try {
      endDate = TIME_FORMAT.parse(endTimeStr);
    } catch (ParseException e) {
      throw new IllegalArgumentException(timeStr, e);
    }
    calendar.setTime(endDate);
    endTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

    return new TimeSchedule(startTime, endTime);
  }

  public int getStartTime() {
    return startTime;
  }

  public int getEndTime() {
    return endTime;
  }

  @Override
  public String toString() {
    if (startTime == -1 && endTime == -1)
      return "NONE";

    Calendar calendar = Calendar.getInstance();

    int startHour = startTime / 60;
    int startMinute = startTime % 60;

    calendar.set(Calendar.HOUR_OF_DAY, startHour);
    calendar.set(Calendar.MINUTE, startMinute);

    String start = TIME_FORMAT.format(calendar.getTime());

    int endHour = endTime / 60;
    int endMinute = endTime % 60;

    calendar.set(Calendar.HOUR_OF_DAY, endHour);
    calendar.set(Calendar.MINUTE, endMinute);

    String end = TIME_FORMAT.format(calendar.getTime());

    return start + "-" + end;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TimeSchedule) {
      TimeSchedule other = (TimeSchedule) o;
      return other.startTime == startTime && other.endTime == endTime;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime);
  }
}
