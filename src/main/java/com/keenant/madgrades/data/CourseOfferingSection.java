package com.keenant.madgrades.data;

import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseOfferingSection {

  private final int termCode;
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final TimeSchedule times;
  private final DaySchedule days;
  private final Room room;
  private final Set<Integer> instructors;

  public CourseOfferingSection(int termCode, int courseNumber,
      SectionType sectionType, int sectionNumber, TimeSchedule times,
      DaySchedule days, Room room, Set<Integer> instructors) {
    this.termCode = termCode;
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.room = room;
    this.instructors = instructors;
  }

  public UUID generateUuid() {
    // todo: this should be all fields i think
    String instructorsStr = instructors.stream().sorted().map(s -> "").collect(Collectors.joining());
    String uniqueStr = termCode + "" + courseNumber + sectionType + sectionNumber + times + days + room + instructorsStr;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
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
