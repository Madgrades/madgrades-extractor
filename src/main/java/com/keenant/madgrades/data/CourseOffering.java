package com.keenant.madgrades.data;

import com.keenant.madgrades.utils.GradeType;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A course offered for a particular term. It may be cross listed under any number of
 * subjects (indicated by {@link this#subjects}) assuming each subject it is cross listed
 * under has the same sections offered.
 */
public class CourseOffering {
  private final int termCode;
  private final int courseNumber;
  private final Map<String, Subject> subjects;
  private final Set<Section> sections;
  private final AtomicReference<String> name;
  private final Map<Integer, Map<GradeType, Integer>> grades = new HashMap<>();

  public CourseOffering(int termCode, int courseNumber, Subject subject, String name, Set<Section> sections) {
    this.termCode = termCode;
    this.courseNumber = courseNumber;
    this.subjects = new HashMap<String, Subject>() {{
      put(subject.getCode(), subject);
    }};
    this.sections = sections;
    this.name = new AtomicReference<>(name);
  }

  public void merge(CourseOffering other) {
    if (termCode != other.termCode || courseNumber != other.courseNumber)
      throw new IllegalArgumentException();

    subjects.putAll(other.subjects);
    sections.addAll(other.sections);
    if (name.get() == null)
      name.set(other.getName().orElse(null));
    for (int sectionNumber : other.grades.keySet()) {
      addGrades(sectionNumber, other.grades.get(sectionNumber));
    }
  }

  /**
   * Generates a unique id. It is based off of the term code, course number, and all
   * subjects that this course was cross listed under.
   *
   * @return the unique id
   */
  public UUID generateUuid() {
    String subjectCodesStr = subjects.keySet().stream()
        .sorted()
        .collect(Collectors.joining());
    String uniqueStr = termCode + "" + courseNumber + subjectCodesStr;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public int getTermCode() {
    return termCode;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public Set<String> getSubjectCodes() {
    return subjects.keySet();
  }

  public Collection<Subject> getSubjects() {
    return subjects.values();
  }

  public Set<Section> getSections() {
    return sections;
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name.get());
  }

  public Map<Integer, Map<GradeType, Integer>> getGrades() {
    return grades;
  }

  public boolean isCrossListed(Set<DirSection> sections) {
    for (Section section : this.sections) {
      boolean matchFound = false;
      for (DirSection other : sections) {
        if (section.isCrossListed(other)) {
          matchFound = true;
          break;
        }
      }

      if (!matchFound) {
        return false;
      }
    }

    return true;
  }

  public void addSubject(Subject subject) {
    subjects.put(subject.getCode(), subject);
  }

  private void addGrades(int sectionNumber, Map<GradeType, Integer> distribution) {
    Map<GradeType, Integer> combine = grades.getOrDefault(sectionNumber, new HashMap<>());
    for (Entry<GradeType, Integer> entry : distribution.entrySet()) {
      combine.put(entry.getKey(), entry.getValue() + combine.getOrDefault(entry.getKey(), 0));
    }
    grades.put(sectionNumber, combine);
  }

  public void addGrades(List<SectionGrades> sectionGradesList) {
    for (SectionGrades sectionGrades : sectionGradesList) {
      addGrades(sectionGrades.getSectionNumber(), sectionGrades.getGrades());
    }
  }
}
