package com.keenant.madgrades.data;

import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import java.util.Objects;
import java.util.Set;

public class CourseOfferingSection {
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final TimeSchedule times;
  private final DaySchedule days;
  private final Room room;
  private final Set<Integer> instructors;

  public CourseOfferingSection(int courseNumber,
      SectionType sectionType, int sectionNumber, TimeSchedule times,
      DaySchedule days, Room room, Set<Integer> instructors) {
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.room = room;
    this.instructors = instructors;
  }

  public boolean isCrossListed(Section other) {
    return courseNumber == other.getCourseNumber() &&
        sectionType == other.getSectionType() &&
        sectionNumber == other.getSectionNumber() &&
        Objects.equals(times, other.getTimes()) &&
        Objects.equals(days, other.getDays()) &&
        Objects.equals(room, other.getRoom()) &&
        Objects.equals(instructors, other.getInstructors());
  }

  @Override
  public String toString() {
    return "CourseOfferingSection{" +
        "courseNumber=" + courseNumber +
        ", sectionType=" + sectionType +
        ", sectionNumber=" + sectionNumber +
        ", times=" + times +
        ", days=" + days +
        ", room=" + room +
        ", instructors=" + instructors +
        '}';
  }
}
