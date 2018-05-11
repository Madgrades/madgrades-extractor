package com.keenant.madgrades.data;

import com.keenant.madgrades.utils.SectionType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public class DirSection {
  private final String subjectCode;
  private final int courseNumber;
  private final SectionType sectionType;
  private final int sectionNumber;
  private final Schedule schedule;
  private final Room room;
  private final Map<Integer, String> instructors;

  public DirSection(String subjectCode, int courseNumber,
      SectionType sectionType, int sectionNumber, @Nullable Schedule schedule,
      @Nullable Room room, @Nullable Integer instructorId, @Nullable String instructorName) {
    this.subjectCode = subjectCode;
    this.courseNumber = courseNumber;
    this.sectionType = sectionType;
    this.sectionNumber = sectionNumber;
    this.schedule = schedule;
    this.room = room;
    this.instructors = new HashMap<>();

    if (instructorId != null)
      instructors.put(instructorId, instructorName);
  }

  public boolean matches(DirSection other) {
    return subjectCode.equals(other.subjectCode) &&
        courseNumber == other.courseNumber &&
        sectionType == other.sectionType &&
        sectionNumber == other.sectionNumber &&
        Objects.equals(schedule, other.schedule) &&
        Objects.equals(room, other.room) &&
        instructors.keySet().equals(other.instructors.keySet());
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

  public Schedule getSchedule() {
    return schedule;
  }

  public Room getRoom() {
    return room;
  }

  public Map<Integer, String> getInstructors() {
    return instructors;
  }

  public void combineInstructors(DirSection other) {
    instructors.putAll(other.getInstructors());
  }

  public Section toSection(int termCode) {
    return new Section(
        termCode,
        courseNumber,
        sectionType,
        sectionNumber,
        schedule,
        room,
        instructors
    );
  }
}
