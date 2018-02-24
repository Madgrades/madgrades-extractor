package com.keenant.madgrades.parser;

import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.SubjectMembershipBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A course offered for a particular term.
 */
public class CourseOffering {
  private final int termCode;
  private final int number;
  private final String name;

  /** all subjects where this course is cross-listed as for this term */
  private final Set<String> subjectCodes = new HashSet<>();
  private final Set<Section> sections = new HashSet<>();
  private final Map<String, Map<GradeType, Integer>> grades = new HashMap<>();

  public CourseOffering(int termCode, int number, String name) {
    this.termCode = termCode;
    this.number = number;
    this.name = name;
  }

  public Set<Entry<String, Map<GradeType, Integer>>> getGrades() {
    return grades.entrySet();
  }

  public Set<Section> getSections() {
    return sections;
  }

  public CourseBean toBean() {
    return new CourseBean(number, name);
  }

  public CourseOfferingBean toCourseOfferingBean(UUID courseOfferingUuid) {
    return new CourseOfferingBean(courseOfferingUuid, termCode, name);
  }

  public Set<SubjectMembershipBean> toSubjectMembershipBeans(UUID courseUuid) {
    return subjectCodes.stream().map(subjectCode -> {
      return new SubjectMembershipBean(subjectCode, courseUuid);
    }).collect(Collectors.toSet());
  }

  public void registerSubject(String subjectCode) {
    subjectCodes.add(subjectCode);
  }

  public Section registerSection(String sectionNum, String sectionType,
      WeekdaySchedule daySchedule, TimeSchedule timeSchedule, Room room) {

    Section newSection = new Section(
        number,
        sectionType,
        sectionNum,
        daySchedule,
        timeSchedule,
        room
    );

    // check if section already exists, return it
    for (Section section : sections) {
      if (section.matches(newSection)) {
        return section;
      }
    }

    sections.add(newSection);
    return newSection;
  }

  public void addGrades(String sectionNum, Map<GradeType, Integer> sectionGrades) {
    // combine grades
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

  public String getName() {
    return name;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CourseOffering) {
      CourseOffering other = (CourseOffering) o;
      return name.equals(other.name) &&
          number == other.number;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, name);
  }

  @Override
  public String toString() {
    return subjectCodes + "/" + number + "/" + name + "/" + sections;
  }

  public boolean hasSubjectCode(String subjectCode) {
    return subjectCodes.contains(subjectCode);
  }
}
