package com.keenant.madgrades.parser;

import com.keenant.madgrades.data.CourseBean;
import com.keenant.madgrades.data.CourseOfferingBean;
import com.keenant.madgrades.data.SubjectMembershipBean;
import java.util.HashMap;
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

  /** all subjects where this course is cross-listed as for this term */
  private final Set<String> subjectCodes = new HashSet<>();
  private final Set<Section> sections = new HashSet<>();
  private final Map<String, Map<GradeType, Integer>> grades = new HashMap<>();

  private String name;

  public CourseOffering(int termCode, int number) {
    this.termCode = termCode;
    this.number = number;
  }

  public void setGrades(Map<String, Map<GradeType, Integer>> grades) {
    this.grades.putAll(grades);
  }

  public boolean sectionsMatch(Set<Section> sections) {
    boolean res = this.sections.size() == sections.size();

    for (Section a : this.sections) {
      boolean match = false;
      for (Section b : sections) {
        if (a.isSameMeeting(b)) {
          match = true;
          break;
        }
      }
      if (!match) {
        res = false;
        break;
      }
    }

    return res;
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
    return new CourseOfferingBean(courseOfferingUuid, termCode, name, sections);
  }

  public Set<SubjectMembershipBean> toSubjectMembershipBeans(UUID courseUuid) {
    return subjectCodes.stream()
        .map(subjectCode -> new SubjectMembershipBean(subjectCode, courseUuid))
        .collect(Collectors.toSet());
  }

  public void registerSubject(String subjectCode) {
    subjectCodes.add(subjectCode);
  }

  public Section registerSection(Section newSection) {
    // check if section already exists, return it
    for (Section section : sections) {
      if (section.isSameMeeting(newSection)) {
        section.registerInstructors(newSection.getInstructors());
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

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getSubjectCodes() {
    return subjectCodes;
  }
}
