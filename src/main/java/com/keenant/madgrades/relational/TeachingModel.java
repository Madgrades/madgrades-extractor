package com.keenant.madgrades.relational;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class TeachingModel {
  public static final Serializer<TeachingModel> SERIALIZER = bean -> Arrays.asList(
      bean.sectionUuid.toString(),
      bean.instructorId
  );
  public static final CsvWriter<TeachingModel> CSV_WRITER = new CsvWriter<>(
      "section_uuid,instructor_id",
      SERIALIZER
  );
  public static final SqlWriter<TeachingModel> SQL_WRITER = new SqlWriter<>(
      "teachings", SERIALIZER
  );

  private UUID sectionUuid;
  private String instructorId;

  public TeachingModel(UUID sectionUuid, String instructorId) {
    this.sectionUuid = sectionUuid;
    this.instructorId = instructorId;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TeachingModel) {
      TeachingModel other = (TeachingModel) o;
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
