package com.keenant.madgrades.directory;

public class Course {
  private int number;
  private String fullName;
  private String shortName;

  public Course(int number) {
    this.number = number;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @Override
  public String toString() {
    return number + " (" + fullName + ", " + shortName + ")";
  }
}
