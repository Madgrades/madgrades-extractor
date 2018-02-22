package com.keenant.madgrades.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Directory {
  private final int termNum;

  /** employee id -> instructor name */
  private final Map<String, String> instructorNames = new HashMap<>();

  /** list of all classes for this term */
  private final List<CourseClass> classes = new ArrayList<>();

  public Directory(int termNum) {
    this.termNum = termNum;
  }

  public void registerInstructor(String id, String name) {
    if (id.length() == 0 || name.length() == 0)
      return;
    instructorNames.put(id, name);
  }

  public void addClass(CourseClass courseClass) {
    this.classes.add(courseClass);
  }

  public void print() {
    for (CourseClass courseClass : classes) {
      System.out.println(courseClass);
    }
  }
}
