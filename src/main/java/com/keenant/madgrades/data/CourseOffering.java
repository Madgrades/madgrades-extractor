package com.keenant.madgrades.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A course offered for a particular term. It may be cross listed under any number of
 * subjects (indicated by {@link this#subjectCodes}) assuming each subject it is cross listed
 * under has the same sections offered.
 */
public class CourseOffering {
  private final int termCode;
  private final int courseNumber;
  private final Set<String> subjectCodes;
  private final Set<CourseOfferingSection> sections;

  public CourseOffering(int termCode, int courseNumber, String subjectCode, Set<CourseOfferingSection> sections) {
    this.termCode = termCode;
    this.courseNumber = courseNumber;
    this.subjectCodes = new HashSet<String>() {{
      add(subjectCode);
    }};
    this.sections = sections;
  }

  /**
   * Generates a unique id. It is based off of the term code, course number, and all
   * subjects that this course was cross listed under.
   *
   * @return the unique id
   */
  public UUID generateUuid() {
    String subjectCodesStr = subjectCodes.stream().sorted().collect(Collectors.joining());
    String uniqueStr = termCode + "" + courseNumber + subjectCodesStr;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public String toString() {
    return "CourseOffering{" +
        "termCode=" + termCode +
        ", courseNumber=" + courseNumber +
        ", subjectCodes=" + subjectCodes +
        ", sections=" + sections +
        '}';
  }

  public int getTermCode() {
    return termCode;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public Set<String> getSubjectCodes() {
    return subjectCodes;
  }

  public Set<CourseOfferingSection> getSections() {
    return sections;
  }

  public boolean isCrossListed(Set<Section> sections) {
    for (CourseOfferingSection section : this.sections) {
      boolean matchFound = false;
      for (Section other : sections) {
        if (section.isCrossListed(other)) {
          matchFound = true;
          break;
        }
      }

      if (!matchFound)
        return false;
    }
    return true;
  }

  public void addSubjectCode(String subjectCode) {
    subjectCodes.add(subjectCode);
  }
}
