package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class TeachingBean {
  public static final Serializer<TeachingBean> SERIALIZER = bean -> Arrays.asList(
      bean.sectionUuid.toString(),
      bean.instructorId
  );
  public static final CsvWriter<TeachingBean> CSV_WRITER = new CsvWriter<>(
      "section_uuid,instructor_id",
      SERIALIZER
  );
  public static final SqlWriter<TeachingBean> SQL_WRITER = new SqlWriter<>(
      "teachings", SERIALIZER
  );

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
