package com.keenant.madgrades.parser;

import com.keenant.madgrades.data.InstructorBean;
import com.keenant.madgrades.data.ScheduleBean;
import com.keenant.madgrades.data.SectionBean;
import com.keenant.madgrades.data.TeachingBean;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Section {
  // required data
  private final String subjectCode;
  private final int courseNumber;
  private final String number;

  private String type;
  private WeekdaySchedule daySchedule;
  private TimeSchedule timeSchedule;
  private Room room;
  private Set<String> instructors = new HashSet<>();

  public Section(String subjectCode, int courseNumber, String number,
      String type,
      WeekdaySchedule daySchedule,
      TimeSchedule timeSchedule, Room room) {
    this.subjectCode = subjectCode;
    this.courseNumber = courseNumber;
    this.number = number;
    this.type = type;
    this.daySchedule = daySchedule;
    this.timeSchedule = timeSchedule;
    this.room = room;
  }

  @Override
  public String toString() {
    return subjectCode + "/" + courseNumber + "/" + type + "-" + number + "/" + timeSchedule + "/" + daySchedule
        + "/" + room + "/" + instructors;
  }

  public SectionBean toBean(UUID sectionOfferingUuid, UUID scheduleUuid) {
    return new SectionBean(
        sectionOfferingUuid,
        number,
        type,
        scheduleUuid
    );
  }

  public ScheduleBean toScheduleBean() {
    return new ScheduleBean(
        timeSchedule,
        daySchedule
    );
  }

  public List<InstructorBean> toInstructorBeans(Map<String, String> instructorNames) {
    return instructors.stream()
        .map(instructorId -> new InstructorBean(instructorId, instructorNames.get(instructorId)))
        .collect(Collectors.toList());
  }

  public List<TeachingBean> toTeachingBeans(UUID sectionUuid) {
    return instructors.stream()
        .map(instructorId -> new TeachingBean(sectionUuid, instructorId))
        .collect(Collectors.toList());
  }

  public boolean matches(Section section) {
    return subjectCode.equals(section.subjectCode) &&
        courseNumber == section.courseNumber &&
        number.equals(section.number) &&
        Objects.equals(type, section.type) &&
        Objects.equals(daySchedule, section.daySchedule) &&
        Objects.equals(timeSchedule, section.timeSchedule) &&
        Objects.equals(room, section.room);
  }

  public void addInstructor(String instructorId) {
    instructors.add(instructorId);
  }

  public String getSubjectCode() {
    return subjectCode;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public String getNumber() {
    return number;
  }

  public String getType() {
    return type;
  }

  public WeekdaySchedule getDaySchedule() {
    return daySchedule;
  }

  public TimeSchedule getTimeSchedule() {
    return timeSchedule;
  }

  public Room getRoom() {
    return room;
  }
}
