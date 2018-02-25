package com.keenant.madgrades.parser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TermReport {
  private final int termCode;

  /** course info -> offerings */
  private final Multimap<Integer, CourseOffering> courses = ArrayListMultimap.create();

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

  public Collection<CourseOffering> getCourses(int courseNumber) {
    return courses.get(courseNumber);
  }

  private Optional<CourseOffering> getCourse(int courseNum, Set<Section> sections) {
    CourseOffering tmp = new CourseOffering(termCode, courseNum);
    for (Section section : sections)
      tmp.registerSection(section);

    // todo: should only return one, ever
    return courses.get(courseNum).stream()
        .filter(course -> course.sectionsMatch(tmp.getSections()))
        .findFirst();
  }

  public Optional<CourseOffering> findCourse(String subjectCode, int courseNum) {
    // todo: should only return one, ever right?
    // also: speed it up with a Multimap subject -> courses
    return courses.values().stream()
        .filter(course -> course.hasSubjectCode(subjectCode))
        .filter(course -> course.getNumber() == courseNum)
        .findFirst();
  }

  public CourseOffering getOrCreateCourse(int courseNum, Set<Section> sections) {
    CourseOffering course = getCourse(courseNum, sections).orElse(null);

    if (course == null) {
      course = new CourseOffering(termCode, courseNum);
      courses.put(courseNum, course);
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
