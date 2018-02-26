package com.keenant.madgrades.data;

import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class Section {
  private final String subjectCode;
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final TimeSchedule times;
  private final DaySchedule days;
  private final Room rooms;
  private final Set<Integer> instructors;

  public Section(String subjectCode, int courseNumber,
      SectionType sectionType, int sectionNumber, TimeSchedule times,
      DaySchedule days, Room rooms, Integer instructorId) {
    this.subjectCode = subjectCode;
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.rooms = rooms;
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

  public Room getRooms() {
    return rooms;
  }

  public Set<Integer> getInstructors() {
    return instructors;
  }

  public void combine(Section other) {
    instructors.addAll(other.getInstructors());
  }
}
