package com.keenant.madgrades.utils;

import com.keenant.madgrades.dir.DirEntry;
import com.keenant.madgrades.dir.SectionDirEntry;
import com.keenant.madgrades.dir.SubjectDirEntry;
import com.keenant.madgrades.dir.TermDirEntry;
import com.keenant.madgrades.fields.DaySchedule;
import com.keenant.madgrades.fields.Room;
import com.keenant.madgrades.fields.SectionType;
import com.keenant.madgrades.fields.TimeSchedule;
import java.util.List;
import java.util.stream.Collectors;

public class Parsers {
  public static DirEntry dirEntry(List<String> row) {
    String joined = row.stream().collect(Collectors.joining());

    // some rows have nothing in them
    if (joined.length() == 0)
      return null;

    // extract subject code
    if (joined.contains("SUBJECT:")) {
      String subjectCode = joined.substring(joined.length() - 4, joined.length() - 1);
      return new SubjectDirEntry(subjectCode);
    }

    // extract term code
    if (joined.contains("TERM:")) {
      int termCode = Integer.parseInt(joined.split(":")[1]);
      return new TermDirEntry(termCode);
    }

    int courseNumber;

    // at this point, we just try parsing an integer. if it
    // works then we know we have a section
    try {
      courseNumber = Integer.parseInt(row.get(1));
    } catch (NumberFormatException e) {
      return null;
    }

    SectionType sectionType = SectionType.valueOf(row.get(2));
    int sectionNumber = Integer.parseInt(row.get(3));
    TimeSchedule times = TimeSchedule.parse(row.get(5));
    DaySchedule days = DaySchedule.parse(row.get(6));
    Room rooms = Room.parse(row.get(7));

    Integer instructorId = null;
    String instructorName = null;

    // rarely there is no instructor
    if (row.get(10).length() > 0) {
      instructorId = Integer.parseInt(row.get(10));
    }

    if (row.get(11).length() > 0) {
      instructorName = row.get(11);
    }

    return new SectionDirEntry(
        courseNumber,
        sectionType,
        sectionNumber,
        times,
        days,
        rooms,
        instructorId,
        instructorName
    );
  }
}
