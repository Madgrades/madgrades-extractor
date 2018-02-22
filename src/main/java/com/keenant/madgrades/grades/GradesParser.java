package com.keenant.madgrades.grades;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradesParser implements TableParser<Grades> {

  @Override
  public Grades parse(List<List<String>> rows) throws ParseException {
    Grades grades = new Grades();

    String department = null;
    String subjectName = null;
    String subjectId = null;
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

        // subjectId is usually an int, except for some (i.e. study abroad = SAB)
        subjectId = split[0];
        lastWasSubjectName = false;
        continue;
      }

      if (joined.contains("CourseTotal")) {
        System.out.println("Should update prev nulls to: " + courseName);
      }

      int courseNum;

      try {
        courseNum = Integer.parseInt(row.get(1));
      } catch (Exception e) {
        continue;
      }

      String sectionNum = row.get(2);
      String gpaCount = row.get(3);
      String gpaAvgStr = row.get(4);
      Map<GradeType, Float> gradeCounts = new HashMap<>();
      for (int i = 0; i < GradeType.values().length; i++) {
        GradeType curr = GradeType.values()[i];

        String percentStr = row.get(5 + i);
        if (percentStr.equals(".") || percentStr.length() == 0) {
          gradeCounts.put(curr, null);
          continue;
        }

        try {
          float percent = Float.parseFloat(percentStr);
          gradeCounts.put(curr, percent);
        } catch (Exception e) {
          // TODO: This shouldn't happen, but we should notify user
          gradeCounts.put(curr,null);
        }
      }

      Float gpaAvg = null;
      if (gpaAvgStr.length() > 0 && !gpaAvgStr.equals("***"))
        gpaAvg = Float.parseFloat(gpaAvgStr);

      System.out.println(department + "/" + subjectId + "/" + courseName + "/" + sectionNum + "/" + gpaAvg + "/" + gradeCounts);

      if (rows.indexOf(row) > 500)
        break;
    }

    return grades;
  }
}
