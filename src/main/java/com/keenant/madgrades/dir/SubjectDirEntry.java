package com.keenant.madgrades.dir;

public class SubjectDirEntry implements DirEntry {
  private final String subjectCode;

  public SubjectDirEntry(String subjectCode) {
    this.subjectCode = subjectCode;
  }

  @Override
  public String toString() {
    return "SubjectDirEntry(" + subjectCode + ")";
  }
}
