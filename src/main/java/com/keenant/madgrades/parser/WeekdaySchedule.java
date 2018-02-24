package com.keenant.madgrades.parser;

import com.keenant.madgrades.Constants;
import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WeekdaySchedule {
  /** the list of days which are part of the schedule */
  private final Set<DayOfWeek> days;

  public WeekdaySchedule(Set<DayOfWeek> days) {
    this.days = days;
  }

  public boolean isScheduled(DayOfWeek day) {
    return days.contains(day);
  }

  public static WeekdaySchedule parse(String days) {
    Set<DayOfWeek> result = new LinkedHashSet<>();

    if (days.length() > 0) {
      String[] daysSplit = days.split(" ");

      for (String day : daysSplit) {
        result.add(Constants.STR_TO_DAY.get(day));
      }
    }

    return new WeekdaySchedule(result);
  }

  public String serialize() {
    return days.stream()
        .map(Constants.DAY_TO_STR::get)
        .collect(Collectors.joining(""));
  }

  @Override
  public String toString() {
    if (days.isEmpty())
      return "NONE";
    return days.stream()
        .map(Constants.DAY_TO_STR::get)
        .collect(Collectors.joining(" "));
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof WeekdaySchedule) {
      WeekdaySchedule other = (WeekdaySchedule) o;
      return other.days.size() == days.size() &&
          other.days.containsAll(days);
    }
    return false;
  }
}
