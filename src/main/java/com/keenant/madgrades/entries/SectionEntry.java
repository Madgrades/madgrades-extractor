package com.keenant.madgrades.entries;

import com.keenant.madgrades.data.DirSection;
import com.keenant.madgrades.data.Schedule;
import com.keenant.madgrades.data.Room;
import com.keenant.madgrades.utils.SectionType;
import javax.annotation.Nullable;

public class SectionEntry implements DirEntry {
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final Schedule schedule;
  private final Room room;
  private final Integer instructorId;
  private final String instructorName;

  public SectionEntry(int courseNumber, SectionType sectionType, int sectionNumber,
      Schedule schedule, Room room, @Nullable Integer instructorId,
      @Nullable String instructorName) {
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.schedule = schedule;
    this.room = room;
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }

  public DirSection toDirSection(String subjectCode) {
    return new DirSection(
        subjectCode,
        courseNumber,
        sectionType,
        sectionNumber,
        schedule,
        room,
        instructorId,
        instructorName
    );
  }
}
