package com.keenant.madgrades.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeSchedule {
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

  private final int startTime;
  private final int endTime;

  public TimeSchedule(int startTime, int endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public int getStartTime() {
    return startTime;
  }

  public int getEndTime() {
    return endTime;
  }

  public static TimeSchedule parse(String timeStr) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    int startTime = -1;
    int endTime = -1;

    if (timeStr.contains(" - ")) {
      String[] timeSplit = timeStr.split(" - ");

      String startTimeStr = timeSplit[0];
      String endTimeStr = timeSplit[1];

      Date startDate = TIME_FORMAT.parse(startTimeStr);
      calendar.setTime(startDate);
      startTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

      Date endDate = TIME_FORMAT.parse(endTimeStr);
      calendar.setTime(endDate);
      endTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    return new TimeSchedule(startTime, endTime);
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
}
