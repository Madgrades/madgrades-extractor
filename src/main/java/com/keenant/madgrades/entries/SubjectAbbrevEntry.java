package com.keenant.madgrades.entries;

public class SubjectAbbrevEntry implements GradesEntry {
  private final String subjectAbbrev;

  public SubjectAbbrevEntry(String subjectAbbrev) {
    this.subjectAbbrev = subjectAbbrev;
  }

  public String getSubjectAbbrev() {
    return subjectAbbrev;
  }
}
