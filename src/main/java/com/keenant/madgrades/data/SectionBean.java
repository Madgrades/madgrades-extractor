package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class SectionBean implements Serializable {
  public static final Serializer<SectionBean> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.courseOfferingUuid.toString(),
      bean.type,
      bean.number,
      bean.roomUuid.toString(),
      bean.scheduleUuid.toString()
  );
  public static final CsvWriter<SectionBean> CSV_WRITER = new CsvWriter<>(
      "uuid,course_offering_uuid,section_type,number,room_uuid,schedule_uuid",
      SERIALIZER
  );
  public static final SqlWriter<SectionBean> SQL_WRITER = new SqlWriter<>(
      "sections", SERIALIZER
  );

  private UUID uuid;
  private UUID courseOfferingUuid;
  private String type;
  private String number;
  private UUID roomUuid;
  private UUID scheduleUuid;

  public SectionBean(UUID courseOfferingUuid, String type, String number, UUID roomUuid, UUID scheduleUuid) {
    this.courseOfferingUuid = courseOfferingUuid;
    this.type = type;
    this.number = number;
    this.roomUuid = roomUuid;
    this.scheduleUuid = scheduleUuid;
    uuid = generateUuid();
  }

  public SectionBean() {

  }

  private UUID generateUuid() {
    String uniqueStr = courseOfferingUuid.toString() + number + type + scheduleUuid.toString();
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SectionBean) {
      SectionBean other = (SectionBean) o;
      return uuid.equals(other.uuid) &&
          courseOfferingUuid.equals(other.courseOfferingUuid) &&
          number.equals(other.number) &&
          type.equals(other.type) &&
          scheduleUuid.equals(other.scheduleUuid);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, courseOfferingUuid, number, type, scheduleUuid);
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
