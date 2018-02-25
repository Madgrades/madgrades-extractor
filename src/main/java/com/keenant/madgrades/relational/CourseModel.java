package com.keenant.madgrades.relational;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseModel {
  public static final Serializer<CourseModel> SERIALIZER = bean -> Arrays.asList(
      bean.uuid.toString(),
      bean.number,
      bean.name
  );
  public static final CsvWriter<CourseModel> CSV_WRITER = new CsvWriter<>(
      "uuid,number,name", SERIALIZER);

  public static final SqlWriter<CourseModel> SQL_WRITER = new SqlWriter<>(
      "courses", SERIALIZER
  );

  private UUID uuid;
  private int number;
  private String name;
  private Set<String> subjectCodes;

  public CourseModel(int number, String name, Set<String> subjectCodes) {
    this.number = number;
    this.name = name;
    this.subjectCodes = subjectCodes;
    uuid = generateUuid();
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
    String subjectCodesStr = subjectCodes.stream().sorted().collect(Collectors.joining());
    String uniqueStr = number + name + subjectCodesStr;
    return UUID.nameUUIDFromBytes(uniqueStr.getBytes());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof CourseModel && Objects.equals(((CourseModel) o).uuid, uuid);
  }

  @Override
  public int hashCode() {
    String subjectCodesStr = subjectCodes.stream().sorted().collect(Collectors.joining());
    return Objects.hash(number, name, subjectCodesStr);
  }

  public void addSubjectCodes(Set<String> subjectCodes) {
    this.subjectCodes.addAll(subjectCodes);
    uuid = generateUuid();
  }

  public Set<String> getSubjectCodes() {
    return subjectCodes;
  }
}
