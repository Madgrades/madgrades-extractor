package com.keenant.madgrades.parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A directory of courses, rooms, instructors for a particular term.
 */
public class TermReport {
  private final int termCode;

  /** course num -> course name -> courses */
  private final Table<Integer, String, List<CourseOffering>> courses = HashBasedTable.create();

  /** employee id -> instructor name */
  private final Map<String, String> instructorNames = new HashMap<>();

  public TermReport(int termCode) {
    this.termCode = termCode;
  }

  public int getTermCode() {
    return termCode;
  }

  public Map<String, String> getInstructorNames() {
    return instructorNames;
  }

  public Collection<CourseOffering> getCourses() {
    return courses.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  public Optional<List<CourseOffering>> getCourses(int courseNum, String name) {
    return Optional.ofNullable(courses.get(courseNum, name));
  }

  public Optional<CourseOffering> getCourse(int courseNum, String name, Set<String> sectionNumbers) {
    List<CourseOffering> courses = getCourses(courseNum, name).orElse(null);

    if (courses == null)
      return Optional.empty();

    return courses.stream()
        .filter(course -> course.sectionsMatch(sectionNumbers))
        .findFirst();
  }

  public Optional<CourseOffering> findCourse(String subjectCode, int courseNum) {
    return courses.row(courseNum).values().stream()
        .flatMap(Collection::stream)
        .filter(course -> course.hasSubjectCode(subjectCode))
        .findFirst();
  }

  public CourseOffering getOrCreateCourse(int courseNum, String name,
      Map<String, Map<GradeType, Integer>> grades) {
    // todo: remove this once ready
    if (name == null)
      throw new IllegalArgumentException();

    CourseOffering course = getCourse(courseNum, name, grades.keySet()).orElse(null);

    if (course == null) {
      course = new CourseOffering(termCode, courseNum, name, grades);

      List<CourseOffering> existing = getCourses(courseNum, name).orElse(null);
      if (existing == null) {
        existing = new ArrayList<>();
        courses.put(courseNum, name, existing);
      }
      existing.add(course);
    }

    return course;
  }

  public void registerInstructor(String id, String name) {
    if (id.length() == 0 || name.length() == 0)
      return;
    instructorNames.put(id, name);
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();

    out.append("Term ").append(termCode).append(":\n");
    out.append("\tCourses: ").append(courses.values().size()).append("\n");
    out.append("\tInstructors: ").append(instructorNames.size());

    return out.toString();
  }
}
