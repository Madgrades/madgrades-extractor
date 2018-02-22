package com.keenant.madgrades.grades;

import java.util.Map;

public class SectionGrades {
  private final int courseNumber;
  private final String sectionNumber;
  private final float gpaAvg;
  private final Map<GradeType, Integer> grades;

  public SectionGrades(int courseNumber, String sectionNumber, float gpaAvg, Map<GradeType, Integer> grades) {
    this.courseNumber = courseNumber;
    this.sectionNumber = sectionNumber;
    this.gpaAvg = gpaAvg;
    this.grades = grades;
  }
}
