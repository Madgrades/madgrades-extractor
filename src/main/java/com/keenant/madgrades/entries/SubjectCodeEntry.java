package com.keenant.madgrades.entries;

public class SubjectCodeEntry implements DirEntry, GradesEntry {
  private final String subjectCode;

  public SubjectCodeEntry(String subjectCode) {
    this.subjectCode = subjectCode;
  }

  public String getSubjectCode() {
    return subjectCode;
  }
}
