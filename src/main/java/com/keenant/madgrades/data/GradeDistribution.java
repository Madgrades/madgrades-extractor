package com.keenant.madgrades.data;

import com.keenant.madgrades.utils.GradeType;
import java.util.Map;

public class GradeDistribution {
  private final CourseOffering courseOffering;
  private final int sectionNumber;
  private final Map<GradeType, Integer> grades;

  public GradeDistribution(CourseOffering courseOffering, int sectionNumber, Map<GradeType, Integer> grades) {
    this.courseOffering = courseOffering;
    this.sectionNumber = sectionNumber;
    this.grades = grades;
  }

  public CourseOffering getCourseOffering() {
    return courseOffering;
  }

  public int getSectionNumber() {
    return sectionNumber;
  }

  public Map<GradeType, Integer> getGrades() {
    return grades;
  }

  public double calculateGpa() {
    double num = 4.0 * grades.get(GradeType.A) +
        3.5 * grades.get(GradeType.AB) +
        3.0 * grades.get(GradeType.B) +
        2.5 * grades.get(GradeType.BC) +
        2.0 * grades.get(GradeType.C) +
        1.0 * grades.get(GradeType.D);
    double denom = grades.get(GradeType.A) +
        grades.get(GradeType.AB) +
        grades.get(GradeType.B) +
        grades.get(GradeType.BC) +
        grades.get(GradeType.C) +
        grades.get(GradeType.D) +
        grades.get(GradeType.F);
    return num / denom;
  }
}
