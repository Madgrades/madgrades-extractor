package com.keenant.madgrades.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Directory {
  private final int termCode;

  private final Map<Integer, Course> courses = new HashMap<>();

  /** list of all sections for this term */
  private final Map<Integer, List<Section>> sections = new HashMap<>();

  /** set of every room present in this directory */
  private final Set<Room> rooms = new HashSet<>();

  /** employee id -> instructor name */
  private final Map<String, String> instructorNames = new HashMap<>();

  public Directory(int termCode) {
    this.termCode = termCode;
  }

  public void registerInstructor(String id, String name) {
    if (id.length() == 0 || name.length() == 0)
      return;
    instructorNames.put(id, name);
  }

  public void registerRoom(Room room) {
    rooms.add(room);
  }

  public void addSection(Section section) {
    if (!courses.containsKey(section.getCourseNumber())) {
      Course course = new Course(section.getCourseNumber());
      courses.put(section.getCourseNumber(), course);
    }

    List<Section> list = sections.getOrDefault(section.getCourseNumber(), new ArrayList<>());
    list.add(section);
    sections.put(section.getCourseNumber(), list);
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();

    int sectionCount = sections.values().stream().mapToInt(List::size).sum();

    out.append("Directory ").append(termCode).append(":\n");
    out.append("\tCourses: ").append(courses.size()).append("\n");
    out.append("\tSections: ").append(sectionCount).append("\n");
    out.append("\tRooms: ").append(rooms.size()).append("\n");
    out.append("\tInstructors: ").append(instructorNames.size());

    return out.toString();
  }
}
