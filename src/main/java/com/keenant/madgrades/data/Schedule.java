package com.keenant.madgrades.data;

import com.google.common.base.Preconditions;
import com.keenant.madgrades.utils.DaySchedule;
import com.keenant.madgrades.utils.TimeSchedule;
import java.util.Objects;
import java.util.UUID;

public class Schedule {
  private final TimeSchedule times;
  private final DaySchedule days;
  private final UUID uuid;

  public Schedule(TimeSchedule times, DaySchedule days) {
    Preconditions.checkNotNull(times);
    Preconditions.checkNotNull(days);
    this.times = times;
    this.days = days;
    uuid = generateUuid();
  }

  private UUID generateUuid() {
    String uniqueStr = times.toString() + days.toString();
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public TimeSchedule getTimes() {
    return times;
  }

  public DaySchedule getDays() {
    return days;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Schedule other = (Schedule) o;
    return Objects.equals(times, other.times) &&
        Objects.equals(days, other.days);
  }

  @Override
  public int hashCode() {
    return Objects.hash(times, days);
  }
}
