package com.keenant.madgrades.data;

import java.util.Objects;

public class Subject {
  private final String code;
  private final String abbreviation;
  private final String name;

  public Subject(String code, String abbreviation, String name) {
    this.code = code;
    this.abbreviation = abbreviation;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Subject subject = (Subject) o;
    return Objects.equals(code, subject.code) &&
        Objects.equals(abbreviation, subject.abbreviation) &&
        Objects.equals(name, subject.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, abbreviation, name);
  }
}
