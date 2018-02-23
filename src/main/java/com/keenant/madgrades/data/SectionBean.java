package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class SectionBean implements Serializable {
  public static final CsvWriter<SectionBean> CSV_WRITER = new CsvWriter<>(
      "uuid,course_offering_uuid,section_type,number,schedule_uuid",
      bean -> Arrays.asList(
          bean.uuid,
          bean.courseOfferingUuid,
          bean.type,
          bean.number,
          bean.scheduleUuid
      ));

  private UUID uuid;
  private UUID courseOfferingUuid;
  private String number;
  private String type;
  private UUID scheduleUuid;

  public SectionBean(UUID courseOfferingUuid, String number, String type, UUID scheduleUuid) {
    this.courseOfferingUuid = courseOfferingUuid;
    this.number = number;
    this.type = type;
    this.scheduleUuid = scheduleUuid;
    uuid = generateUuid();
  }

  public SectionBean() {

  }

  private UUID generateUuid() {
    String uniqueStr = courseOfferingUuid.toString() + number + type + scheduleUuid.toString();
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  public UUID getCourseOfferingUuid() {
    return courseOfferingUuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getNumber() {
    return number;
  }

  public String getType() {
    return type;
  }

  public UUID getScheduleUuid() {
    return scheduleUuid;
  }
}
