package com.keenant.madgrades.parser;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public class DirParser implements TableParser {
  private final int termCode;

  public DirParser(int termCode) {
    this.termCode = termCode;
  }

  @Override
  public TermReport parse(List<List<String>> rows) throws ParseException {
    TermReport report = new TermReport(this.termCode);

    String subjectCode = null;

    for (List<String> row : rows) {
      String joined = row.stream().collect(Collectors.joining(""));

      if (joined.contains("SUBJECT")) {
        subjectCode = joined.substring(joined.length() - 4, joined.length() - 1);
      }

      if (joined.contains("TERM:")) {
        String[] termSplit = joined.split(":");
        int termCode = Integer.parseInt(termSplit[termSplit.length - 1]);

        if (termCode != this.termCode) {
          throw new RuntimeException("termCode mismatch");
        }
      }

      int courseNum;

      try {
        courseNum = Integer.parseInt(row.get(1));
      } catch (Exception e) {
        // skip this, not a section
        continue;
      }

      // 0: unknown value
      // 4: not used because its usually just "1'

      String sectionType = row.get(2);
      String sectionNum = row.get(3);
      String timeStr = row.get(5);
      String daysStr = row.get(6);
      String roomStr = row.get(7);
      String instructorId = row.get(10);
      String instructorName = row.get(11);

      Room room = Room.parse(roomStr);
      WeekdaySchedule daySchedule = WeekdaySchedule.parse(daysStr);
      TimeSchedule timeSchedule = TimeSchedule.parse(timeStr);

      Course course = report.getOrCreateCourse(subjectCode, courseNum);
      Section section = course.registerSection(
          sectionNum,
          sectionType,
          daySchedule,
          timeSchedule,
          room
      );
      section.addInstructor(instructorId);
      report.registerRoom(room);
      report.registerInstructor(instructorId, instructorName);
    }

    return report;
  }
}
