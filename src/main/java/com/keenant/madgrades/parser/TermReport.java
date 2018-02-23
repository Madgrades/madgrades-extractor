package com.keenant.madgrades.parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A directory of courses, rooms, instructors for a particular term.
 */
public class TermReport {
  private final int termCode;

  /** subject -> course num -> course */
  private final Table<String, Integer, Course> courses = HashBasedTable.create();

  /** set of every room present in this directory */
  private final Set<Room> rooms = new HashSet<>();

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

  public Set<Room> getRooms() {
    return rooms;
  }

  public Collection<Course> getCourses() {
    return courses.values();
  }

  public Optional<Course> getCourse(String subjectCode, int courseNum) {
    if (!courses.contains(subjectCode, courseNum))
      return Optional.empty();
    return Optional.ofNullable(courses.get(subjectCode, courseNum));
  }

  public Course getOrCreateCourse(String subjectCode, int courseNum) {
    Course course = getCourse(subjectCode, courseNum).orElse(null);
    if (course == null) {
      course = new Course(subjectCode, courseNum);
      courses.put(subjectCode, courseNum, course);
    }
    return course;
  }

  public void registerInstructor(String id, String name) {
    if (id.length() == 0 || name.length() == 0)
      return;
    instructorNames.put(id, name);
  }

  public void registerRoom(Room room) {
    rooms.add(room);
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();

    out.append("Term ").append(termCode).append(":\n");
    out.append("\tCourses: ").append(courses.values().size()).append("\n");
    out.append("\tRooms: ").append(rooms.size()).append("\n");
    out.append("\tInstructors: ").append(instructorNames.size());

    return out.toString();
  }
}
