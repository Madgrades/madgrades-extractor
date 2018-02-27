package com.keenant.madgrades.entries;

public class SubjectEntry implements DirEntry, GradesEntry {
  private final String subjectCode;

  public SubjectEntry(String subjectCode) {
    this.subjectCode = subjectCode;
  }

  public String getSubjectCode() {
    return subjectCode;
  }
}
