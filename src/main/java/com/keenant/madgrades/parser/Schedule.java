package com.keenant.madgrades.parser;

import java.util.Objects;

// todo: needed?
public class Schedule {
  private final TimeSchedule time;
  private final WeekdaySchedule days;

  public Schedule(TimeSchedule time, WeekdaySchedule days) {
    this.time = time;
    this.days = days;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Schedule) {
      Schedule other = (Schedule) o;
      return time.equals(other.time) && days.equals(other.days);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, days);
  }
}
