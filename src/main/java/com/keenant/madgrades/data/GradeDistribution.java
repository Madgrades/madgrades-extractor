package com.keenant.madgrades.data;

import com.keenant.madgrades.utils.GradeType;
import java.util.Map;

public class GradeDistribution {
  private final int sectionNumber;
  private final Map<GradeType, Integer> grades;

  public GradeDistribution(int sectionNumber, Map<GradeType, Integer> grades) {
    this.sectionNumber = sectionNumber;
    this.grades = grades;
  }

  public int getSectionNumber() {
    return sectionNumber;
  }

  public Map<GradeType, Integer> getGrades() {
    return grades;
  }
}
