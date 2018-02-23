package com.keenant.madgrades.data;

import com.keenant.madgrades.parser.TimeSchedule;
import com.keenant.madgrades.parser.WeekdaySchedule;
import com.keenant.madgrades.util.CsvWriter;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleBean {
  public static final CsvWriter<ScheduleBean> CSV_WRITER = new CsvWriter<>(
      "start_time,end_time,mon,tues,wed,thurs,fri,sat,sun",
      bean -> Arrays.asList(
          bean.startTime,
          bean.endTime,
          bean.mon,
          bean.tues,
          bean.wed,
          bean.thurs,
          bean.fri,
          bean.sat,
          bean.sun
      ));

  private int startTime;
  private int endTime;
  private boolean mon;
  private boolean tues;
  private boolean wed;
  private boolean thurs;
  private boolean fri;
  private boolean sat;
  private boolean sun;
  private UUID uuid;

  public ScheduleBean(int startTime, int endTime, boolean mon, boolean tues, boolean wed,
      boolean thurs, boolean fri, boolean sat, boolean sun) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.mon = mon;
    this.tues = tues;
    this.wed = wed;
    this.thurs = thurs;
    this.fri = fri;
    this.sat = sat;
    this.sun = sun;
    uuid = generateUuid();
  }

  private UUID generateUuid() {
    String uniqueStr = Stream.of(
        startTime,
        endTime,
        mon,
        tues,
        wed,
        thurs,
        fri,
        sat,
        sun
    ).map(Object::toString).collect(Collectors.joining(""));
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public ScheduleBean(TimeSchedule time, WeekdaySchedule days) {
    this(time.getStartTime(), time.getEndTime(),
        days.isScheduled(DayOfWeek.MONDAY),
        days.isScheduled(DayOfWeek.TUESDAY),
        days.isScheduled(DayOfWeek.WEDNESDAY),
        days.isScheduled(DayOfWeek.THURSDAY),
        days.isScheduled(DayOfWeek.FRIDAY),
        days.isScheduled(DayOfWeek.SATURDAY),
        days.isScheduled(DayOfWeek.SUNDAY)
    );
  }

  public ScheduleBean() {

  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ScheduleBean) {
      ScheduleBean other = (ScheduleBean) o;
      return startTime == other.startTime && endTime == other.endTime &&
          mon == other.mon && tues == other.tues && wed == other.wed &&
          thurs == other.thurs && fri == other.fri && sat == other.sat &&
          sun == other.sun;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  public UUID getUuid() {
    return uuid;
  }
}
