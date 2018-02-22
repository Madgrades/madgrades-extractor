package com.keenant.madgrades.directory;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryParser implements TableParser<Directory> {
  private final int termCode;

  public DirectoryParser(int termCode) {
    this.termCode = termCode;
  }

  @Override
  public Directory parse(List<List<String>> rows) throws ParseException {
    Directory directory = new Directory(this.termCode);

    int subjectId = -1;

    for (List<String> row : rows) {
      String joined = row.stream().collect(Collectors.joining(""));

      if (joined.contains("SUBJECT")) {
        String subjectStr = joined.substring(joined.length() - 4, joined.length() - 1);
        subjectId = Integer.parseInt(subjectStr);
      }

      if (joined.contains("TERM:")) {
        String[] termSplit = joined.split(":");
        int termCode = Integer.parseInt(termSplit[termSplit.length - 1]);

        if (termCode != this.termCode) {
          throw new RuntimeException("termCode mismatch");
        }
      }

      int courseNum = -1;

      try {
        courseNum = Integer.parseInt(row.get(1));
      } catch (Exception e) {
        // skip this, not a section
        continue;
      }

      // unknown values: 0

      String sectionType = row.get(2);
      String sectionNum = row.get(3);
      String classNum = row.get(4);
      String timeStr = row.get(5);
      String daysStr = row.get(6);
      String roomStr = row.get(7);
      String instructorId = row.get(10);
      String instructorName = row.get(11);

      Room facility = Room.parse(roomStr);

      TimeSchedule timeSchedule = TimeSchedule.parse(timeStr);
      WeekdaySchedule daySchedule = WeekdaySchedule.parse(daysStr);

      CourseClass courseClass = new CourseClass(
          termCode,
          subjectId,
          courseNum,
          sectionNum,
          sectionType,
          classNum,
          daySchedule,
          timeSchedule,
          facility,
          instructorId
      );

      directory.registerInstructor(instructorId, instructorName);
      directory.addClass(courseClass);
    }

    return directory;
  }
}
