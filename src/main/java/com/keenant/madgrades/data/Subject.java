package com.keenant.madgrades.data;

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
}
