package com.keenant.madgrades.data;

import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Course {
  private final int courseNumber;
  private final Set<CourseOffering> courseOfferings = new HashSet<>();

  public Course(int courseNumber) {
    this.courseNumber = courseNumber;
  }

  public int getCourseNumber() {
    return courseNumber;
  }

  public Set<CourseOffering> getCourseOfferings() {
    return courseOfferings;
  }

  /**
   * Generate a unique id. It is based on the course number and the
   * UUID of the first offering of this course.
   *
   * @return the unique id.
   */
  public UUID generateUuid() {
    // get first course offering
    CourseOffering firstOffering = courseOfferings.stream()
        .sorted(Comparator.comparingInt(CourseOffering::getTermCode))
        .findFirst().orElse(null);

    int hash = Objects.hash(courseNumber, firstOffering.generateUuid());
    return UUID.nameUUIDFromBytes((hash + "").getBytes());
  }

  public boolean isCourse(CourseOffering offering) {
    return courseNumber == offering.getCourseNumber() &&
        !Sets.intersection(getSubjects(), offering.getSubjects()).isEmpty();
  }

  public Set<Subject> getSubjects() {
    return courseOfferings.stream()
        .flatMap(o -> o.getSubjects().stream())
        .collect(Collectors.toSet());
  }

  public void addCourseOffering(CourseOffering offering) {
    courseOfferings.add(offering);
  }
}
