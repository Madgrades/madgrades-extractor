package com.keenant.madgrades.parser;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class GradesParser implements TableParser {
  private final TermReport report;

  public GradesParser(TermReport report) {
    this.report = report;
  }

  private void addGrades(Map<String, Map<GradeType, Integer>> backlog, String sectionNum,
      Map<GradeType, Integer> grades) {
    Map<GradeType, Integer> existing = backlog.computeIfAbsent(sectionNum, s -> new LinkedHashMap<>());

    // add counts to existing section
    for (Entry<GradeType, Integer> entry : grades.entrySet()) {
      existing.put(entry.getKey(), entry.getValue() + existing.getOrDefault(entry.getKey(), 0));
    }
  }

  private void registerCourse(String subjectCode, int courseNum, String name, Map<String, Map<GradeType, Integer>> backlog) {
    Map<String, Map<GradeType, Integer>> grades = new HashMap<>(backlog);
    CourseOffering offering = report.getOrCreateCourse(courseNum, name, grades);
    offering.registerSubject(subjectCode);
  }

  @Override
  public TermReport parse(List<List<String>> rows) throws ParseException {
    String department = null;
    int lastCourseNum = -1;
    String subjectName = null;
    String subjectCode = null;
    boolean lastWasSubjectName = false;

    Map<String, Map<GradeType, Integer>> backlog = new HashMap<>();

    for (List<String> row : rows) {
      String joined = row.stream().collect(Collectors.joining(""));

      String courseName = null;

      if (row.get(0).length() > 2 || joined.contains("CourseTotal"))
        courseName = row.get(0);

      // schools are 3 chars
      if (joined.length() == 3) {
        department = joined;
        continue;
      }

      if (joined.contains("Section#")) {
        subjectName = row.get(0);
        lastWasSubjectName = true;
        continue;
      }
      else if (lastWasSubjectName) {
        String[] split = row.get(0).split(" ");

        // subject is usually an int, except for some (i.e. study abroad = SAB)
        subjectCode = split[0];
        lastWasSubjectName = false;
        continue;
      }

      if (joined.contains("CourseTotal")) {
        registerCourse(subjectCode, lastCourseNum, courseName, backlog);
        backlog.clear();
        continue;
      }

      int courseNum;

      try {
        courseNum = Integer.parseInt(row.get(1));
      } catch (Exception e) {
        continue;
      }

      String sectionNum = row.get(2);
      int gpaCount = Integer.parseInt(row.get(3));

      Map<GradeType, Integer> grades = new LinkedHashMap<>();

      for (int i = 0; i < GradeType.values().length; i++) {
        GradeType curr = GradeType.values()[i];

        String percentStr = row.get(5 + i);
        if (percentStr.equals(".") || percentStr.length() == 0) {
          grades.put(curr, 0);
          continue;
        }

        try {
          // percent that got this gpa, between 0 and 1]
          float percent = Float.parseFloat(percentStr) / 100.0F;

          // multiply by total grades reported
          int count = Math.round(percent * (float) gpaCount);
          grades.put(curr, count);
        } catch (Exception e) {
          // TODO: This shouldn't happen, but we should notify user
          e.printStackTrace();
          System.exit(-1);
        }
      }

      // add grades to backlog
      addGrades(backlog, sectionNum, grades);

      if (courseName != null) {
        registerCourse(subjectCode, courseNum, courseName, backlog);
        backlog.clear();
      }

      lastCourseNum = courseNum;
    }

    return report;
  }
}
