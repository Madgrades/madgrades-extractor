package com.keenant.madgrades.directory;

public class CourseClass {
  private final int termCode;
  private final int subjectId;
  private final int courseNumber;
  private final String sectionNumber;
  private final String sectionType;
  private final String number;
  private final WeekdaySchedule daySchedule;
  private final TimeSchedule timeSchedule;
  private final Room room;
  private final String instructorId;

  public CourseClass(int termCode, int subjectId, int courseNumber, String sectionNumber,
      String sectionType, String number, WeekdaySchedule daySchedule, TimeSchedule timeSchedule,
      Room room, String instructorId) {
    this.termCode = termCode;
    this.subjectId = subjectId;
    this.courseNumber = courseNumber;
    this.sectionNumber = sectionNumber;
    this.sectionType = sectionType;
    this.number = number;
    this.daySchedule = daySchedule;
    this.timeSchedule = timeSchedule;
    this.room = room;
    this.instructorId = instructorId;
  }

  @Override
  public String toString() {
    return subjectId + "/" + courseNumber + "/" + sectionType + sectionNumber + "/" + number + "/" + timeSchedule + "/" + daySchedule
        + "/" + room + "/" + instructorId;
  }
}
