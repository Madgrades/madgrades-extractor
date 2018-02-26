package com.keenant.madgrades.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Term {
  private final int termCode;
  private final List<Section> sections = new ArrayList<>();
  private final List<GradeDistribution> sectionGrades = new ArrayList<>();

  public Term(int termCode) {
    this.termCode = termCode;
  }

  public Stream<Section> sections(String subjectCode, int courseNumber) {
    return sections.stream()
        .filter(section -> section.getSubjectCode().equals(subjectCode))
        .filter(section -> section.getCourseNumber() == courseNumber);
  }

  public Optional<Section> sections(String subjectCode, int courseNumber, int sectionNumber) {
    return sections(subjectCode, courseNumber)
        .filter(section -> section.getSectionNumber() == sectionNumber)
        .findFirst();
  }

  public void addSection(Section section) {
    Section existing = sections(section.getSubjectCode(), section.getCourseNumber(),
        section.getSectionNumber()).orElse(null);

    if (existing == null)
      this.sections.add(section);
    else
      existing.combine(section);
  }

}
