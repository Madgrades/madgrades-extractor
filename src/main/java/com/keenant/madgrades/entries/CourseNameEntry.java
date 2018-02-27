package com.keenant.madgrades.entries;

public class CourseNameEntry implements GradesEntry {
  private final String courseName;

  public CourseNameEntry(String courseName) {
    this.courseName = courseName;
  }

  public String getCourseName() {
    return courseName;
  }
}
