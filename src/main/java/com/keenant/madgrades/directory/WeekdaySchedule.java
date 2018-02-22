package com.keenant.madgrades.directory;

import com.keenant.madgrades.Constants;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WeekdaySchedule {
  /** the list of days which are part of the schedule */
  private final Set<DayOfWeek> days;

  public WeekdaySchedule(Set<DayOfWeek> days) {
    this.days = days;
  }

  public static WeekdaySchedule parse(String days) {
    Set<DayOfWeek> result = new HashSet<>();

    if (days.length() > 0) {
      String[] daysSplit = days.split(" ");

      for (String day : daysSplit) {
        result.add(Constants.STR_TO_DAY.get(day));
      }
    }

    return new WeekdaySchedule(result);
  }

  @Override
  public String toString() {
    if (days.isEmpty())
      return "NONE";
    return Arrays.stream(DayOfWeek.values())
        .filter(days::contains)
        .map(Constants.DAY_TO_STR::get)
        .collect(Collectors.joining(" "));
  }
}
