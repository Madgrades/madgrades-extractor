package com.keenant.madgrades.parser;

import com.keenant.madgrades.relational.InstructorModel;
import com.keenant.madgrades.relational.ScheduleModel;
import com.keenant.madgrades.relational.SectionModel;
import com.keenant.madgrades.relational.TeachingModel;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Section {
  // required data
  private final int courseNumber;
  private final String number;

  private String type;
  private WeekdaySchedule daySchedule;
  private TimeSchedule timeSchedule;
  private Room room;
  private Set<String> instructors = new HashSet<>();

  public Section(int courseNumber, String type, String number, WeekdaySchedule daySchedule,
      TimeSchedule timeSchedule, Room room) {
    this.courseNumber = courseNumber;
    this.type = type;
    this.number = number;
    this.daySchedule = daySchedule;
    this.timeSchedule = timeSchedule;
    this.room = room;
  }

  @Override
  public String toString() {
    return courseNumber + "/" + type + "-" + number + "/" + timeSchedule + "/" + daySchedule
        + "/" + room + "/" + instructors;
  }

  public SectionModel toBean(UUID sectionOfferingUuid, UUID roomUuid, UUID scheduleUuid) {
    return new SectionModel(
        sectionOfferingUuid,
        type,
        number,
        roomUuid,
        scheduleUuid
    );
  }

  public ScheduleModel toScheduleBean() {
    return new ScheduleModel(
        timeSchedule,
        daySchedule
    );
  }

  public List<InstructorModel> toInstructorBeans(Map<String, String> instructorNames) {
    return instructors.stream()
        .map(instructorId -> new InstructorModel(instructorId, instructorNames.get(instructorId)))
        .collect(Collectors.toList());
  }

  public List<TeachingModel> toTeachingBeans(UUID sectionUuid) {
    return instructors.stream()
        .map(instructorId -> new TeachingModel(sectionUuid, instructorId))
        .collect(Collectors.toList());
  }

  public boolean isSameMeeting(Section other) {
    return courseNumber == other.courseNumber &&
        number.equals(other.number) &&
        type.equals(other.type) &&
        daySchedule.equals(other.daySchedule) &&
        timeSchedule.equals(other.timeSchedule) &&
        room.equals(other.room);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Section) {
      Section other = (Section) o;
      return isSameMeeting(other) &&
          instructors.size() == other.instructors.size() &&
          instructors.containsAll(other.instructors);
    }
    return false;
  }

  public void registerInstructor(String instructorId) {
    if (instructorId.length() > 0)
      instructors.add(instructorId);
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

  public Set<String> getInstructors() {
    return instructors;
  }

  public void registerInstructors(Set<String> instructors) {
    this.instructors.addAll(instructors);
  }
}
