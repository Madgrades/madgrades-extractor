package com.keenant.madgrades.dir;

import com.keenant.madgrades.data.Section;
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
  private final Room room;
  private final Integer instructorId;
  private final String instructorName;

  public SectionDirEntry(int courseNumber, SectionType sectionType, int sectionNumber,
      TimeSchedule times, DaySchedule days, Room room, @Nullable Integer instructorId,
      @Nullable String instructorName) {
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.times = times;
    this.days = days;
    this.room = room;
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }

  public Section toSection(String subjectCode) {
    return new Section(
        subjectCode,
        courseNumber,
        sectionType,
        sectionNumber,
        times,
        days,
        room,
        instructorId
    );
  }

  @Override
  public String toString() {
    return "SectionDirEntry(" + courseNumber + "/" + sectionType + "-" + sectionNumber + ")";
  }
}
