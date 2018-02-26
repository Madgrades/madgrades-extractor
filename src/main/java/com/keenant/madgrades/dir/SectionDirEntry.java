package com.keenant.madgrades.dir;

import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import javax.annotation.Nullable;

public class SectionDirEntry implements DirEntry {
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final TimeSchedule times;
  private final DaySchedule days;
  private final Room rooms;
  private final Integer instructorId;
  private final String instructorName;

  public SectionDirEntry(int courseNumber, SectionType sectionType, int sectionNumber,
      TimeSchedule times, DaySchedule days, Room rooms, @Nullable Integer instructorId,
      @Nullable String instructorName) {
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.rooms = rooms;
    this.instructorId = instructorId;
    this.instructorName = instructorName;
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

  public int getInstructorId() {
    return instructorId;
  }

  public String getInstructorName() {
    return instructorName;
  }

  @Override
  public String toString() {
    return "SectionDirEntry(" + courseNumber + "/" + sectionType + "-" + sectionNumber + ")";
  }
}
