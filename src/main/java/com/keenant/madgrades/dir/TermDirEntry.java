package com.keenant.madgrades.dir;

public class TermDirEntry implements DirEntry {
  private final int termCode;

  public TermDirEntry(int termCode) {
    this.termCode = termCode;
  }

  @Override
  public String toString() {
    return "TermDirEntry(" + termCode + ")";
  }
}
