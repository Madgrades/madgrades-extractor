package com.keenant.madgrades.entries;

import com.keenant.madgrades.utils.GradeType;
import java.util.Map;

public class SectionGradesEntry implements GradesEntry {
  private final int courseNumber;
  private final int sectionNumber;
  private final Map<GradeType, Integer> gradeDistribution;

  public SectionGradesEntry(int courseNumber, int sectionNumber, Map<GradeType, Integer> gradeDistribution) {
    this.courseNumber = courseNumber;
    this.sectionNumber = sectionNumber;
    this.gradeDistribution = gradeDistribution;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public int getSectionNumber() {
    return sectionNumber;
  }

  public Map<GradeType, Integer> getGradeDistribution() {
    return gradeDistribution;
  }
}
