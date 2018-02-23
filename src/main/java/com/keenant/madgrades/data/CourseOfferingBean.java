package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import java.util.Arrays;
import java.util.UUID;

public class CourseOfferingBean {
  public static final CsvWriter<CourseOfferingBean> CSV_WRITER = new CsvWriter<>(
      "uuid,course_uuid,term_code,short_name",
      bean -> Arrays.asList(
          bean.uuid,
          bean.courseUuid,
          bean.termCode,
          bean.shortName
      ));

  private UUID uuid;
  private UUID courseUuid;
  private int termCode;
  private String shortName;

  public CourseOfferingBean(UUID courseUuid, int termCode, String shortName) {
    this.courseUuid = courseUuid;
    this.termCode = termCode;
    this.shortName = shortName;
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

  public String getShortName() {
    return shortName;
  }
}
