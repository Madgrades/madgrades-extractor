package com.keenant.madgrades.data;

import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public class Section {
  private final String subjectCode;
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final TimeSchedule times;
  private final DaySchedule days;
  private final Room room;
  private final Set<Integer> instructors;

  public Section(String subjectCode, int courseNumber,
      SectionType sectionType, int sectionNumber, TimeSchedule times,
      DaySchedule days, Room room, @Nullable Integer instructorId) {
    this.subjectCode = subjectCode;
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.room = room;
    this.instructors = new HashSet<>();

    if (instructorId != null)
      instructors.add(instructorId);
  }

  public String getSubjectCode() {
    return subjectCode;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public SectionType getSectionType() {
    return sectionType;
  }

  public int getSectionNumber() {
    return sectionNumber;
  }

  public TimeSchedule getTimes() {
    return times;
  }

  public DaySchedule getDays() {
    return days;
  }

  public Room getRoom() {
    return room;
  }

  public Set<Integer> getInstructors() {
    return instructors;
  }

  public void combine(Section other) {
    instructors.addAll(other.getInstructors());
  }

  public boolean isCrossListed(Section other) {
    return !subjectCode.equals(other.subjectCode) &&
        courseNumber == other.courseNumber &&
        sectionType == other.sectionType &&
        sectionNumber == other.sectionNumber &&
        Objects.equals(times, other.times) &&
        Objects.equals(days, other.days) &&
        Objects.equals(room, other.room) &&
        Objects.equals(instructors, other.instructors);
  }

  public CourseOfferingSection toCourseOfferingSection() {
    return new CourseOfferingSection(
        courseNumber,
        sectionType,
        sectionNumber,
        times,
        days,
        room,
        instructors
    );
  }
}
