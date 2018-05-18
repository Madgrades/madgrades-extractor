package com.keenant.madgrades.tools;

import com.google.common.collect.ImmutableMap;
import com.keenant.madgrades.data.Course;
import com.keenant.madgrades.data.CourseOffering;
import com.keenant.madgrades.data.GradeDistribution;
import com.keenant.madgrades.data.Instructor;
import com.keenant.madgrades.data.Schedule;
import com.keenant.madgrades.data.Section;
import com.keenant.madgrades.data.Subject;
import com.keenant.madgrades.data.Teaching;
import com.keenant.madgrades.utils.GradeType;
import com.keenant.madgrades.utils.NoArgObjectMapper;
import com.keenant.madgrades.utils.ObjectMapper;
import com.keenant.madgrades.data.Room;
import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

public class Mappers {
  public static final NoArgObjectMapper<Course> COURSE = (course) ->
      new LinkedHashMap<String, Object>() {{
        put("uuid", course.generateUuid().toString());
        put("name", course.getName().orElse(null));
        put("number", course.getCourseNumber());
      }};

  public static final ObjectMapper<CourseOffering, Course> COURSE_OFFERING = (offering, course) ->
      new LinkedHashMap<String, Object>() {{
        put("uuid", offering.generateUuid().toString());
        put("course_uuid", course.generateUuid().toString());
        put("term_code", offering.getTermCode());
        put("name", offering.getName().orElse(null));
      }};

  public static final NoArgObjectMapper<Instructor> INSTRUCTOR = (instructor) ->
      new LinkedHashMap<String, Object>() {{
        put("id", instructor.getId());
        put("name", instructor.getName());
      }};

  public static final ObjectMapper<Section, CourseOffering> SECTION = (section, offering) ->
      new LinkedHashMap<String, Object>() {{
        put("uuid", section.generateUuid(offering).toString());
        put("course_offering_uuid", offering.generateUuid().toString());
        put("section_type", section.getSectionType().name());
        put("number", section.getSectionNumber());
        put("room_uuid", section.getRoom().map(Room::getUuid).map(UUID::toString).orElse(null));
        put("schedule_uuid", section.getSchedule().getUuid().toString());
      }};

  public static final NoArgObjectMapper<Schedule> SCHEDULE = (schedule) ->
      new LinkedHashMap<String, Object>() {{
        put("uuid", schedule.getUuid().toString());
        put("start_time", schedule.getTimes().getStartTime());
        put("end_time", schedule.getTimes().getEndTime());
        put("mon", schedule.getDays().isScheduled(DayOfWeek.MONDAY));
        put("tues", schedule.getDays().isScheduled(DayOfWeek.TUESDAY));
        put("wed", schedule.getDays().isScheduled(DayOfWeek.WEDNESDAY));
        put("thurs", schedule.getDays().isScheduled(DayOfWeek.THURSDAY));
        put("fri", schedule.getDays().isScheduled(DayOfWeek.FRIDAY));
        put("sat", schedule.getDays().isScheduled(DayOfWeek.SATURDAY));
        put("sun", schedule.getDays().isScheduled(DayOfWeek.SUNDAY));
      }};

  public static final NoArgObjectMapper<Room> ROOM = (room) ->
      new LinkedHashMap<String, Object>() {{
        put("uuid", room.getUuid().toString());
        put("facility_code", room.getFacilityCode());
        put("room_code", room.getRoomCode());
      }};

  public static final NoArgObjectMapper<Teaching> TEACHING = (teaching) -> ImmutableMap.of(
      "instructor_id", teaching.getInstructorId(),
      "section_uuid", teaching.getSection().generateUuid(teaching.getOffering()).toString()
  );

  public static final ObjectMapper<String, CourseOffering> SUBJECT_MEMBERSHIP = (subjectCode, offering) -> ImmutableMap.of(
      "subject_code", subjectCode,
      "course_offering_uuid", offering.generateUuid().toString()
  );

  public static final NoArgObjectMapper<GradeDistribution> GRADE_DISTRIBUTION = (grades) ->
      new LinkedHashMap<String, Object>() {{
        double gpa = grades.calculateGpa();
        put("course_offering_uuid", grades.getCourseOffering().generateUuid().toString());
        put("section_number", grades.getSectionNumber());
        put("gpa", Double.isNaN(gpa) ? null : gpa);
        for (GradeType type : GradeType.values()) {
          put(type.name().toLowerCase() + "_count", grades.getGrades().getOrDefault(type, 0));
        }
      }};

  public static final NoArgObjectMapper<Subject> SUBJECT = (subject) ->
      new LinkedHashMap<String, Object>() {{
        put("code", subject.getCode());
        put("name", subject.getName());
        put("abbreviation", subject.getAbbreviation());
      }};
}
