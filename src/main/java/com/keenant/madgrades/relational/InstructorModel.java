package com.keenant.madgrades.relational;

import com.keenant.madgrades.util.CsvWriter;
import com.keenant.madgrades.util.Serializer;
import com.keenant.madgrades.util.SqlWriter;
import java.util.Arrays;
import java.util.Objects;

public class InstructorModel {
  public static final Serializer<InstructorModel> SERIALIZER = bean -> Arrays.asList(
      bean.id,
      bean.name
  );
  public static final CsvWriter<InstructorModel> CSV_WRITER = new CsvWriter<>(
      "id,name", SERIALIZER
  );
  public static final SqlWriter<InstructorModel> SQL_WRITER = new SqlWriter<>(
      "instructors", SERIALIZER
  );

  private String id;
  private String name;

  public InstructorModel(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public InstructorModel() {

  }

  @Override
  public boolean equals(Object o) {
    return o instanceof InstructorModel && id.equals(((InstructorModel) o).id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
