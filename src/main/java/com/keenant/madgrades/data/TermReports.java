package com.keenant.madgrades.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.keenant.madgrades.tools.Mappers;
import com.keenant.madgrades.utils.GradeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class TermReports {
  private final Map<Integer, Term> terms = new HashMap<>();
  private final Table<String, Integer, String> fullCourseNames = HashBasedTable.create();

  public Term getOrCreateTerm(int termCode) {
    return terms.computeIfAbsent(termCode, Term::new);
  }

  public Multimap<String, Map<String, Object>> generateTables(Set<Subject> scrapedSubjects) {
    Multimap<String, Map<String, Object>> tables = ArrayListMultimap.create();

    Map<String, Subject> subjects = new HashMap<>();

    List<Course> courses = generateCourses();

    Set<Integer> instructorsAdded = new HashSet<>();
    Set<UUID> schedulesAdded = new HashSet<>();
    Set<UUID> roomsAdded = new HashSet<>();

    for (Course course : courses) {
      // first we set the course to have a full name
      boolean fullNameFound = false;
      for (Subject subject : course.subjects()) {
        if (subject.getAbbreviation() == null) {
          continue;
        }
        Map<Integer, String> fullNames = fullCourseNames.row(subject.getAbbreviation());

        String fullName = fullNames.get(course.getCourseNumber());

        if (fullName != null) {
          fullNameFound = true;
          course.setName(fullName);
          break;
        }
      }

      if (!fullNameFound) {
        // this doesn't seem to ever happen, but just in case...
        // TODO? System.out.println("No full name for: " + course.subjectCodes() + " " + course.getCourseNumber());
      }

      // now we can save it to the table
      tables.put("courses", Mappers.COURSE.map(course));

      for (CourseOffering offering : course.getCourseOfferings()) {
        tables.put("course_offerings", Mappers.COURSE_OFFERING.map(offering, course));

        for (Subject subject : offering.getSubjects()) {
          subjects.put(subject.getCode(), subject);
          tables.put("subject_memberships", Mappers.SUBJECT_MEMBERSHIP.map(subject.getCode(), offering));
        }

        for (Entry<Integer, Map<GradeType, Integer>> grades : offering.getGrades().entrySet()) {
          GradeDistribution dist = new GradeDistribution(offering, grades.getKey(), grades.getValue());
          tables.put("grade_distributions", Mappers.GRADE_DISTRIBUTION.map(dist));
        }

        for (Section section : offering.getSections()) {
          tables.put("sections", Mappers.SECTION.map(section, offering));

          Schedule schedule = section.getSchedule();
          Room room = section.getRoom().orElse(null);

          if (!schedulesAdded.contains(schedule.getUuid())) {
            tables.put("schedules", Mappers.SCHEDULE.map(schedule));
            schedulesAdded.add(schedule.getUuid());
          }

          if (room != null && !roomsAdded.contains(room.getUuid())) {
            tables.put("rooms", Mappers.ROOM.map(room));
            roomsAdded.add(room.getUuid());
          }

          for (Entry<Integer, String> entry : section.getInstructors().entrySet()) {
            Teaching teaching = new Teaching(entry.getKey(), section, offering);
            tables.put("teachings", Mappers.TEACHING.map(teaching));

            // TODO: Sometimes people change their names, this overwrites any changes :/
            if (instructorsAdded.contains(entry.getKey()))
              continue;
            instructorsAdded.add(entry.getKey());

            Instructor instructor = new Instructor(entry.getKey(), entry.getValue());
            tables.put("instructors", Mappers.INSTRUCTOR.map(instructor));
          }
        }
      }
    }

    for (Subject subject : scrapedSubjects) {
      subjects.put(subject.getCode(), subject);
    }

    for (Subject subject : subjects.values()) {
      tables.put("subjects", Mappers.SUBJECT.map(subject));
    }

    return tables;
  }

  private List<Course> generateCourses() {
    List<Course> courses = new ArrayList<>(5000);

    for (Term term : terms.values()) {
      List<CourseOffering> courseOfferings = term.generateCourseOfferings();

      for (CourseOffering offering : courseOfferings) {
        Course course = courses.stream()
            .filter(c -> c.isCourse(offering))
            .findFirst()
            .orElse(null);

        if (course == null) {
          course = new Course(offering.getCourseNumber());
          courses.add(course);
        }

        course.addCourseOffering(offering);
      }
    }

    Set<Course> remove = new HashSet<>();

    // post process merging
    for (Course a : courses) {
      if (remove.contains(a))
        continue;

      for (Course b : courses) {
        if (a.equals(b))
          continue;
        if (remove.contains(b))
          continue;

        if (a.isCourse(b)) {
          // TODO? System.out.println("Merging: " + a + " + " + b);
          for (CourseOffering offering : b.getCourseOfferings())
            a.addCourseOffering(offering);
          remove.add(b);
        }
      }
    }

    courses.removeAll(remove);

    return courses;
  }

  public void setFullCourseName(String subjectAbbrev, int courseNumber, String name) {
    fullCourseNames.put(subjectAbbrev, courseNumber, name);
  }
}
