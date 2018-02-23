package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class TeachingBean {
  public static final CsvWriter<TeachingBean> CSV_WRITER = new CsvWriter<>(
      "section_uuid,instructor_id",
      bean -> Arrays.asList(
          bean.sectionUuid,
          bean.instructorId
      ));

  private UUID sectionUuid;
  private String instructorId;

  public TeachingBean(UUID sectionUuid, String instructorId) {
    this.sectionUuid = sectionUuid;
    this.instructorId = instructorId;
  }

  public TeachingBean() {

  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TeachingBean) {
      TeachingBean other = (TeachingBean) o;
      return sectionUuid.equals(other.sectionUuid) &&
          instructorId.equals(other.instructorId);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sectionUuid, instructorId);
  }
}
