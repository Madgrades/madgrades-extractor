package com.keenant.madgrades.data;

public class Teaching {
  private final int instructorId;
  private final Section section;
  private final CourseOffering offering;

  public Teaching(int instructorId, Section section, CourseOffering offering) {
    this.instructorId = instructorId;
    this.section = section;
    this.offering = offering;
  }

  public int getInstructorId() {
    return instructorId;
  }

  public Section getSection() {
    return section;
  }

  public CourseOffering getOffering() {
    return offering;
  }
}
