package com.keenant.madgrades.utils;

import com.keenant.madgrades.Constants;
import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DaySchedule {
  /** the list of days which are part of the schedule */
  private final Set<DayOfWeek> days;

  private DaySchedule(Set<DayOfWeek> days) {
    this.days = days;
  }

  public static DaySchedule parse(String days) {
    Set<DayOfWeek> result = new LinkedHashSet<>();

    if (days.length() > 0) {
      String[] daysSplit = days.split(" ");

      for (String dayStr : daysSplit) {
        DayOfWeek day = Constants.STR_TO_DAY.get(dayStr);

        if (day == null) {
          throw new IllegalArgumentException(days);
        }

        result.add(day);
      }
    }

    return new DaySchedule(result);
  }

  public boolean isScheduled(DayOfWeek day) {
    return days.contains(day);
  }

  @Override
  public String toString() {
    if (days.isEmpty())
      return null;

    return days.stream()
        .map(Constants.DAY_TO_STR::get)
        .collect(Collectors.joining(" "));
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DaySchedule) {
      DaySchedule other = (DaySchedule) o;
      return days.equals(other.days);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return days.hashCode();
  }
}
