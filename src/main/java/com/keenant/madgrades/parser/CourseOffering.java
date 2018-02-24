package com.keenant.madgrades.parser;

import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.SubjectMembershipBean;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
  private final Map<String, Map<GradeType, Integer>> grades;

  public CourseOffering(int termCode, int number, String name,
      Map<String, Map<GradeType, Integer>> grades) {
    this.termCode = termCode;
    this.number = number;
    this.name = name;
    this.grades = grades;
  }

  public Set<String> getSectionNumbers() {
    return grades.keySet();
  }

  public boolean sectionsMatch(Set<String> sectionNumbers) {
    return this.grades.size() == sectionNumbers.size() &&
        this.grades.keySet().containsAll(sectionNumbers);
  }

  public Set<Entry<String, Map<GradeType, Integer>>> getGrades() {
    return grades.entrySet();
  }

  public Set<Section> getSections() {
    return sections;
  }

  public CourseBean toBean() {
    return new CourseBean(number, name, subjectCodes);
  }

  public CourseOfferingBean toCourseOfferingBean(UUID courseOfferingUuid) {
    return new CourseOfferingBean(courseOfferingUuid, termCode, name, grades.keySet());
  }

  public Set<SubjectMembershipBean> toSubjectMembershipBeans(UUID courseUuid) {
    return subjectCodes.stream()
        .map(subjectCode -> new SubjectMembershipBean(subjectCode, courseUuid))
        .collect(Collectors.toSet());
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

  public String getName() {
    return name;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public String toString() {
    return subjectCodes + "/" + number + "/" + name + "/" + grades.keySet();
  }

  public boolean hasSubjectCode(String subjectCode) {
    return subjectCodes.contains(subjectCode);
  }
}
