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
      bean.number,
      bean.name
  );
  public static final CsvWriter<CourseBean> CSV_WRITER = new CsvWriter<>(
      "uuid,number,name", SERIALIZER);

  public static final SqlWriter<CourseBean> SQL_WRITER = new SqlWriter<>(
      "courses", SERIALIZER
  );

  private UUID uuid;
  private int number;
  private String name;

  public CourseBean(int number, String name) {
    this.number = number;
    this.name = name;
    uuid = generateUuid();
  }

  public CourseBean() {

  }

  public UUID getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public int getNumber() {
    return number;
  }

  public UUID generateUuid() {
    String uniqueStr = number + name;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof CourseBean && Objects.equals(((CourseBean) o).uuid, uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, name);
  }
}
