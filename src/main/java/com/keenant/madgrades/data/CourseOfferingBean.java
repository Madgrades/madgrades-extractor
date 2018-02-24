package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.UUID;

public class CourseOfferingBean {
  public static final Serializer<CourseOfferingBean> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.courseUuid.toString(),
      bean.termCode,
      bean.name
  );
  public static final CsvWriter<CourseOfferingBean> CSV_WRITER = new CsvWriter<>(
      "uuid,course_uuid,term_code,name", SERIALIZER
  );
  public static final SqlWriter<CourseOfferingBean> SQL_WRITER = new SqlWriter<>(
      "course_offerings", SERIALIZER
  );

  private UUID uuid;
  private UUID courseUuid;
  private int termCode;
  private String name;

  public CourseOfferingBean(UUID courseUuid, int termCode, String name) {
    this.courseUuid = courseUuid;
    this.termCode = termCode;
    this.name = name;
    uuid = generateUuid();
  }

  public CourseOfferingBean() {

  }

  private UUID generateUuid() {
    String uniqueStr = courseUuid.toString() + termCode;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
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
