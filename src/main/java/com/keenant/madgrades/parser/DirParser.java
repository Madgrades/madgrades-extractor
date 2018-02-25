package com.keenant.madgrades.parser;

import com.keenant.madgrades.TableParser;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DirParser implements TableParser {
  private final TermReport report;

  public DirParser(TermReport report) {
    this.report = report;
  }

  private void registerSections(String subjectCode, int courseNum, Set<Section> backlog) {
    if (courseNum < 0)
      return;

    CourseOffering offering = report.getOrCreateCourse(courseNum, backlog);

    // add subject to course
    offering.registerSubject(subjectCode);

    // add every section
    for (Section section : backlog)
      offering.registerSection(section);
  }

  @Override
  public TermReport parse(List<List<String>> rows) throws ParseException {
    String subjectCode = null;

    String backlogSubjectCode = null;
    int backlogCourseNum = -1;
    Set<Section> backlog = new HashSet<>();

    for (List<String> row : rows) {
      String joined = row.stream().collect(Collectors.joining(""));

      if (joined.contains("SUBJECT")) {
        subjectCode = joined.substring(joined.length() - 4, joined.length() - 1);
      }

      if (joined.contains("TERM:")) {
        String[] termSplit = joined.split(":");
        int termCode = Integer.parseInt(termSplit[termSplit.length - 1]);

        if (termCode != report.getTermCode()) {
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

      if (backlogCourseNum != courseNum) {
        registerSections(backlogSubjectCode, backlogCourseNum, backlog);
        backlog.clear();
      }

      Section section = new Section(
          courseNum,
          sectionType,
          sectionNum,
          daySchedule,
          timeSchedule,
          room
      );
      section.registerInstructor(instructorId);
      backlog.add(section);

      report.registerInstructor(instructorId, instructorName);

      backlogCourseNum = courseNum;
      backlogSubjectCode = subjectCode;
    }

    return report;
  }
}
