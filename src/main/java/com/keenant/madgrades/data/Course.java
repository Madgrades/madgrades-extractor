package com.keenant.madgrades.data;

import java.util.HashSet;
import java.util.Set;

public class Course {
  private final int courseNumber;
  private final Set<String> subjectCodes;

  public Course(int courseNumber) {
    this.courseNumber = courseNumber;
    this.subjectCodes = new HashSet<>();
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public Set<String> getSubjectCodes() {
    return subjectCodes;
  }

  public void addSubjectCode(String subjectCode) {
    subjectCodes.add(subjectCode);
  }
}
