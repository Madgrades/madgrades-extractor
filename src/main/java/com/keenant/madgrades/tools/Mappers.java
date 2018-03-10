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
import com.keenant.madgrades.utils.Room;
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

  public static final ObjectMapper<GradeDistribution, CourseOffering> GRADE_DISTRIBUTION = (grades, offering) ->
      new LinkedHashMap<String, Object>() {{
          put("course_offering_uuid", offering.generateUuid().toString());
          put("section_number", grades.getSectionNumber());
          for (Entry<GradeType, Integer> entry : grades.getGrades().entrySet()) {
            put(entry.getKey().name().toLowerCase() + "_count", entry.getValue());
          }
      }};

  public static final NoArgObjectMapper<Subject> SUBJECT = (subject) -> ImmutableMap.of(
      "code", subject.getCode(),
      "name", subject.getName(),
      "abbreviation", subject.getAbbreviation()
  );
}
