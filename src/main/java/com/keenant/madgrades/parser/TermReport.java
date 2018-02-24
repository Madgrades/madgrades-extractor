package com.keenant.madgrades.parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
  private final Table<Integer, String, CourseOffering> courses = HashBasedTable.create();

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
    return courses.values();
  }

  public Optional<CourseOffering> getCourse(int courseNum, String name) {
    return Optional.ofNullable(courses.get(courseNum, name));
  }

  public Optional<CourseOffering> findCourse(String subjectCode, int courseNum) {
    // todo: simplify this entire function to one line, this was done just for testing
    Stream<CourseOffering> s = courses.row(courseNum).values().stream()
        .filter(course -> course.hasSubjectCode(subjectCode));

    List<CourseOffering> result = s.collect(Collectors.toList());
    if (result.size() > 1)
      throw new IllegalArgumentException();

    return result.stream().findFirst();
  }

  public CourseOffering getOrCreateCourse(int courseNum, String name) {
    // todo: remove this once ready
    if (name == null)
      throw new IllegalArgumentException();

    CourseOffering course = getCourse(courseNum, name).orElse(null);
    if (course == null) {
      course = new CourseOffering(termCode, courseNum, name);
      courses.put(courseNum, name, course);
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
