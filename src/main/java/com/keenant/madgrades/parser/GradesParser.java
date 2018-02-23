package com.keenant.madgrades.parser;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradesParser implements TableParser {
  private final TermReport report;

  public GradesParser(TermReport report) {
    this.report = report;
  }

  @Override
  public TermReport parse(List<List<String>> rows) throws ParseException {
    String department = null;
    int lastCourseNum = -1;
    String subjectName = null;
    String subjectCode = null;
    boolean lastWasSubjectName = false;

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
        Course lastCourse = report.getOrCreateCourse(subjectCode, lastCourseNum);
        lastCourse.setShortName(courseName);
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

      Course course = report.getOrCreateCourse(subjectCode, courseNum);
      course.setGrades(sectionNum, grades);

      if (courseName != null)
        course.setShortName(courseName);

      lastCourseNum = courseNum;
    }

    return report;
  }
}
