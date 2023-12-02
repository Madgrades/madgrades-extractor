package com.keenant.madgrades.tools;

import com.keenant.madgrades.data.Schedule;
import com.keenant.madgrades.entries.CourseNameEntry;
import com.keenant.madgrades.entries.DirEntry;
import com.keenant.madgrades.entries.GradesEntry;
import com.keenant.madgrades.entries.SectionEntry;
import com.keenant.madgrades.entries.SectionGradesEntry;
import com.keenant.madgrades.entries.SubjectAbbrevEntry;
import com.keenant.madgrades.entries.SubjectCodeEntry;
import com.keenant.madgrades.entries.SubjectNameEntry;
import com.keenant.madgrades.utils.DaySchedule;
import com.keenant.madgrades.utils.GradeType;
import com.keenant.madgrades.utils.PdfRow;
import com.keenant.madgrades.data.Room;
import com.keenant.madgrades.utils.SectionType;
import com.keenant.madgrades.utils.TimeSchedule;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parse {
  /**
   * Parse a row to a stream of dir report entries.
   *
   * @param row the table row
   * @return the stream of entries processed from the row
   */
  public static Stream<DirEntry> dirEntry(PdfRow row, int termCode) {
    String text = row.getText().orElseThrow(() -> new IllegalStateException("Full text required for dir entries"));
    List<String> cols = row.getColumns();

    String joined = cols.stream().collect(Collectors.joining());

    // some rows have nothing in them
    if (joined.length() == 0)
      return Stream.empty();

    // extract subject code
    if (joined.toUpperCase().contains("SUBJECT")) {
      int lParen = joined.lastIndexOf("(");
      int rParen = joined.lastIndexOf(")");
      String subjectCode = joined.substring(lParen + 1, rParen).trim();
      String subjectName = text.substring(8, text.lastIndexOf("(") - 1).trim();

      return Stream.of(new SubjectCodeEntry(subjectCode), new SubjectNameEntry(subjectName));
    }

    int courseNumber;

    // at this point, we just try parsing an integer. if it
    // works then we know we have a section
    try {
      courseNumber = Integer.parseInt(cols.get(1));
    } catch (NumberFormatException e) {
      return Stream.empty();
    }

    int sectionTypeIndex = 2;
    int sectionNumberIndex = 3;
    if (termCode == 1124) {
      sectionTypeIndex = 3;
      sectionNumberIndex = 2;
    }

    SectionType sectionType = SectionType.valueOf(cols.get(sectionTypeIndex));
    int sectionNumber = Integer.parseInt(cols.get(sectionNumberIndex));
    TimeSchedule times = TimeSchedule.parse(cols.get(5));
    DaySchedule days = DaySchedule.parse(cols.get(6));
    Room rooms = Room.parse(cols.get(7));

    Integer instructorId = null;
    String instructorName = null;

    // rarely there is no instructor
    if (cols.get(10).length() > 0) {
      instructorId = Integer.parseInt(cols.get(10));
    }

    if (cols.get(11).length() > 0) {
      instructorName = cols.get(11);

      // some names contain role first
      if (instructorName.contains("/")) {
        String[] roleAndName = instructorName.split("/");
        if (roleAndName.length == 2) {
          instructorName = roleAndName[1];
        }
      }

      // some names are "LAST, FIRST", we reorder them to "FIRST LAST"
      if (instructorName.contains(",")) {
        String[] names = instructorName.split(",");
        if (names.length == 2) {
          instructorName = names[1] + " " + names[0];
        }
      }
      instructorName = instructorName.trim();
    }

    Schedule schedule = new Schedule(times, days);

    SectionEntry sectionEntry = new SectionEntry(
        courseNumber,
        sectionType,
        sectionNumber,
        schedule,
        rooms,
        instructorId,
        instructorName
    );

    return Stream.of(sectionEntry);
  }

  /**
   * Parse a row to a stream of grade entries.
   *
   * NOTE: Course names are separate from grade entries, they arrive AFTER the sequence of
   * section which the name correspond to.
   *
   * @param row the table row
   * @return the stream of entries processed from the row
   */
  public static Stream<GradesEntry> gradeEntry(PdfRow row) {
    List<String> cols = row.getColumns();

    String joined = cols.stream().collect(Collectors.joining());

    // some rows have nothing in them
    if (joined.length() == 0)
      return Stream.empty();

    // line which has this also has the subject code
    if (joined.contains("GradesGPA") && cols.get(0).length() > 3) {
      String subjectCode = cols.get(0).substring(0, 3);
      String subjectAbbrev = cols.get(0).substring(4, cols.get(0).length());
      return Stream.of(new SubjectCodeEntry(subjectCode), new SubjectAbbrevEntry(subjectAbbrev));
    }

    String courseName = cols.get(0);

    int courseNumber;
    int sectionNumber;

    try {
      courseNumber = Integer.parseInt(cols.get(1));

      // note: sometimes section number is 00A, we don't add those grades
      sectionNumber = Integer.parseInt(cols.get(2));
    } catch (NumberFormatException e) {
      // no int present, but "Course Total" is, that means we just got the name of the course
      if (!courseName.isEmpty() && joined.contains("Total")) {
        return Stream.of(new CourseNameEntry(courseName));
      }
      return Stream.empty();
    }

    double gradeCount = (double) Integer.parseInt(cols.get(3));

    Map<GradeType, Integer> grades = new LinkedHashMap<>();

    for (int i = 0; i < GradeType.values().length; i++) {
      GradeType gradeType = GradeType.values()[i];
      double percent;

      try {
        percent = Double.parseDouble(cols.get(i + 5));
      } catch (NumberFormatException e) {
        grades.put(gradeType, 0);
        continue;
      }

      int count = (int) Math.round(gradeCount * (percent / 100.0));
      grades.put(gradeType, count);
    }

    SectionGradesEntry entry = new SectionGradesEntry(courseNumber, sectionNumber, grades);

    if (courseName.isEmpty()) {
      return Stream.of(entry);
    }
    else {
      return Stream.of(entry, new CourseNameEntry(courseName));
    }
  }
}
