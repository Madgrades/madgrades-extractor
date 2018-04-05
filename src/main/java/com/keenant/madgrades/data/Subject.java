package com.keenant.madgrades.data;

import java.util.Objects;

public class Subject {
  private final String name;
  private final String abbreviation;
  private final String code;

  public Subject(String name, String abbreviation, String code) {
    this.name = name;
    this.abbreviation = abbreviation;
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public String getCode() {
    return code;
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
    return Objects.equals(name, subject.name) &&
        Objects.equals(abbreviation, subject.abbreviation) &&
        Objects.equals(code, subject.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, abbreviation, code);
  }
}
