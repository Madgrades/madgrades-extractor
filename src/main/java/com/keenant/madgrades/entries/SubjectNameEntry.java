package com.keenant.madgrades.entries;

public class SubjectNameEntry implements DirEntry {
  private final String subjectName;

  public SubjectNameEntry(String subjectName) {
    this.subjectName = subjectName;
  }

  public String getSubjectName() {
    return subjectName;
  }
}
