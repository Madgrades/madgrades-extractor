package com.keenant.madgrades.relational;

import com.keenant.madgrades.parser.Section;
import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseOfferingModel {
  public static final Serializer<CourseOfferingModel> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.courseUuid.toString(),
      bean.termCode,
      bean.name
  );
  public static final CsvWriter<CourseOfferingModel> CSV_WRITER = new CsvWriter<>(
      "uuid,course_uuid,term_code,name", SERIALIZER
  );
  public static final SqlWriter<CourseOfferingModel> SQL_WRITER = new SqlWriter<>(
      "course_offerings", SERIALIZER
  );

  private UUID uuid;
  private UUID courseUuid;
  private int termCode;
  private String name;

  private Set<Section> sections; // this is just used to distinguish from other courses

  public CourseOfferingModel(UUID courseUuid, int termCode, String name, Set<Section> sections) {
    this.courseUuid = courseUuid;
    this.termCode = termCode;
    this.name = name;
    this.sections = sections;
    uuid = generateUuid();
  }

  private UUID generateUuid() {
    // todo: this is not good probably
    String sectionNumbersStr = sections.stream().map(Object::toString).sorted().collect(Collectors.joining());
    String uniqueStr = courseUuid.toString() + termCode + name + sectionNumbersStr;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CourseOfferingModel) {
      CourseOfferingModel other = (CourseOfferingModel) o;
      return uuid.equals(other.uuid);
    }
    return false;
  }

  @Override
  public int hashCode() {
    String sectionNumbersStr = sections.stream().map(Object::toString).sorted().collect(Collectors.joining());
    return Objects.hash(uuid, courseUuid, termCode, name, sectionNumbersStr);
  }

  public UUID getUuid() {
    return uuid;
  }

  public UUID getCourseUuid() {
    return courseUuid;
  }

  public int getTermCode() {
    return termCode;
  }

  public String getName() {
    return name;
  }
}
