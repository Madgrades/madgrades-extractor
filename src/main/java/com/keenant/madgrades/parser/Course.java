package com.keenant.madgrades.parser;

import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.GradeDistributionBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Course {
  private final String subjectCode;
  private final int number;
  private final Set<Section> sections = new HashSet<>();
  private final Map<String, Map<GradeType, Integer>> grades = new HashMap<>();

  private String shortName;

  public Course(String subjectCode, int number) {
    this.subjectCode = subjectCode;
    this.number = number;
  }

  public Set<Entry<String, Map<GradeType, Integer>>> getGrades() {
    return grades.entrySet();
  }

  public Set<Section> getSections() {
    return sections;
  }

  public CourseBean toBean() {
    return new CourseBean(subjectCode, number);
  }

  public CourseOfferingBean toCourseOfferingBean(UUID courseUuid, int termCode) {
    return new CourseOfferingBean(courseUuid, termCode, shortName);
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void registerSection(Section section) {
    sections.add(section);
  }

  public Section registerSection(String sectionNum, String sectionType, WeekdaySchedule daySchedule,
      TimeSchedule timeSchedule, Room room) {

    Section newSection = new Section(
        subjectCode,
        number,
        sectionNum,
        sectionType,
        daySchedule,
        timeSchedule,
        room
    );

    for (Section section : sections) {
      if (section.matches(newSection)) {
        return section;
      }
    }

    sections.add(newSection);
    return newSection;
  }

  public void setGrades(String sectionNum, Map<GradeType, Integer> sectionGrades) {
    // very rarely (couple times per PDF), there are multiple grade entries for the same section, we combine them
    if (grades.containsKey(sectionNum)) {
      Map<GradeType, Integer> existingGrades = grades.get(sectionNum);
      for (Entry<GradeType, Integer> entry : sectionGrades.entrySet()) {
        int sum = entry.getValue() + existingGrades.get(entry.getKey());
        existingGrades.put(entry.getKey(), sum);
      }
      return;
    }
    grades.put(sectionNum, sectionGrades);
  }

  public String getSubjectCode() {
    return subjectCode;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Course) {
      Course other = (Course) o;
      return Objects.equals(subjectCode, other.subjectCode) &&
          number == other.number;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(subjectCode, number);
  }

  @Override
  public String toString() {
    return Integer.toString(number);
  }

  public String getShortName() {
    return shortName;
  }
}
