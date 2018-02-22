package com.keenant.madgrades.directory;

public class Section {
  private final int termCode;
  private final String subjectId;
  private final int courseNumber;
  private final String sectionNumber;
  private final String sectionType;
  private final WeekdaySchedule daySchedule;
  private final TimeSchedule timeSchedule;
  private final Room room;
  private final String instructorId;

  public Section(int termCode, String subjectId, int courseNumber, String sectionNumber,
      String sectionType, WeekdaySchedule daySchedule, TimeSchedule timeSchedule,
      Room room, String instructorId) {
    this.termCode = termCode;
    this.subjectId = subjectId;
    this.courseNumber = courseNumber;
    this.sectionNumber = sectionNumber;
    this.sectionType = sectionType;
    this.daySchedule = daySchedule;
    this.timeSchedule = timeSchedule;
    this.room = room;
    this.instructorId = instructorId;
  }

  @Override
  public String toString() {
    return subjectId + "/" + courseNumber + "/" + sectionType + "-" + sectionNumber + "/" + timeSchedule + "/" + daySchedule
        + "/" + room + "/" + instructorId;
  }

  public int getTermCode() {
    return termCode;
  }

  public String getSubjectId() {
    return subjectId;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public String getSectionNumber() {
    return sectionNumber;
  }

  public String getSectionType() {
    return sectionType;
  }

  public WeekdaySchedule getDaySchedule() {
    return daySchedule;
  }

  public TimeSchedule getTimeSchedule() {
    return timeSchedule;
  }

  public Room getRoom() {
    return room;
  }

  public String getInstructorId() {
    return instructorId;
  }
}
