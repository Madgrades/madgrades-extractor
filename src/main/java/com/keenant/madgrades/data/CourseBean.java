package com.keenant.madgrades.data;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class CourseBean {
  public static final Serializer<CourseBean> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.subjectCode,
      bean.number
  );
  public static final CsvWriter<CourseBean> CSV_WRITER = new CsvWriter<>(
      "uuid,subject_code,number", SERIALIZER);

  public static final SqlWriter<CourseBean> SQL_WRITER = new SqlWriter<>(
      "courses", SERIALIZER
  );

  private UUID uuid;
  private String subjectCode;
  private int number;

  public CourseBean(String subjectCode, int number) {
    this.subjectCode = subjectCode;
    this.number = number;
    uuid = generateUuid();
  }

  public CourseBean() {

  }

  public UUID getUuid() {
    return uuid;
  }

  public String getSubjectCode() {
    return subjectCode;
  }

  public int getNumber() {
    return number;
  }

  public UUID generateUuid() {
    String uniqueStr = subjectCode + number;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof CourseBean && Objects.equals(((CourseBean) o).uuid, uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subjectCode, number);
  }
}
