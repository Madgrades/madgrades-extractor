package com.keenant.madgrades.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.keenant.madgrades.tools.Mappers;
import com.keenant.madgrades.utils.GradeType;
import com.keenant.madgrades.utils.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class Reports {
  private final Map<Integer, Term> terms = new HashMap<>();

  public Term getOrCreateTerm(int termCode) {
    return terms.computeIfAbsent(termCode, Term::new);
  }

  public Multimap<String, Map<String, Object>> generateTables() {
    Multimap<String, Map<String, Object>> tables = ArrayListMultimap.create();

    List<Course> courses = generateCourses();

    Set<Integer> instructorsAdded = new HashSet<>();
    Set<UUID> schedulesAdded = new HashSet<>();
    Set<UUID> roomsAdded = new HashSet<>();

    for (Course course : courses) {
      tables.put("courses", Mappers.COURSE.map(course));

      for (CourseOffering offering : course.getCourseOfferings()) {
        tables.put("course_offerings", Mappers.COURSE_OFFERING.map(offering, course));

        for (String subjectCode : offering.getSubjectCodes()) {
          tables.put("subject_memberships", Mappers.SUBJECT_MEMBERSHIP.map(subjectCode, offering));
        }

        for (Entry<Integer, Map<GradeType, Integer>> grades : offering.getGrades().entrySet()) {
          GradeDistribution dist = new GradeDistribution(grades.getKey(), grades.getValue());
          tables.put("grade_distributions", Mappers.GRADE_DISTRIBUTION.map(dist, offering));
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

    return tables;
  }

  private List<Course> generateCourses() {
    List<Course> result = new ArrayList<>(5000);

    for (Term term : terms.values()) {
      List<CourseOffering> courseOfferings = term.generateCourseOfferings();

      for (CourseOffering offering : courseOfferings) {
        Course course = result.stream()
            .filter(c -> c.isCourse(offering))
            .findFirst()
            .orElse(null);

        if (course == null) {
          course = new Course(offering.getCourseNumber());
          result.add(course);
        }

        course.addCourseOffering(offering);
      }
    }

    return result;
  }
}
