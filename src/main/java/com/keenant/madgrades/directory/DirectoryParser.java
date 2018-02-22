package com.keenant.madgrades.directory;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
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

    String subjectId = null;

    for (List<String> row : rows) {
      String joined = row.stream().collect(Collectors.joining(""));

      if (joined.contains("SUBJECT")) {
        String subjectStr = joined.substring(joined.length() - 4, joined.length() - 1);
        subjectId = subjectStr;
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

      TimeSchedule timeSchedule = TimeSchedule.parse(timeStr);
      WeekdaySchedule daySchedule = WeekdaySchedule.parse(daysStr);

      Section section = new Section(
          termCode,
          subjectId,
          courseNum,
          sectionNum,
          sectionType,
          daySchedule,
          timeSchedule,
          room,
          instructorId
      );

      directory.registerRoom(room);
      directory.registerInstructor(instructorId, instructorName);
      directory.addSection(section);
    }

    return directory;
  }
}
