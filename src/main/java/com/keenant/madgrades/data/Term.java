package com.keenant.madgrades.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.keenant.madgrades.dir.DirEntry;
import com.keenant.madgrades.dir.SectionDirEntry;
import com.keenant.madgrades.dir.SubjectDirEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Term {
  private final int termCode;
  private final Multimap<String, Section> sections = ArrayListMultimap.create(10, 1000); // todo

  public Term(int termCode) {
    this.termCode = termCode;
  }

  @Override
  public String toString() {
    return "Term{" +
        "termCode=" + termCode +
        '}';
  }

  public List<CourseOffering> generateCourseOfferings() {
    List<CourseOffering> result = new ArrayList<>();

    Set<Integer> courseNumbers = courseNumbers().collect(Collectors.toSet());

    Set<Section> ignore = new HashSet<>();

    for (int courseNumber : courseNumbers) {
      Multimap<String, Section> sections = sections(courseNumber);

      // list of course offerings for this course number
      // i.e. [CS 252/ECE 252, DS 252]
      List<CourseOffering> offerings = new ArrayList<>();

      for (String subjectCode : sections.keys()) {
        Set<Section> courseSections = sections(subjectCode, courseNumber).collect(Collectors.toSet());

        if (ignore.containsAll(courseSections))
          continue;

        // find a course offering for this course number that has the exact same
        // set of sections
        Optional<CourseOffering> crossListed = offerings.stream()
            .filter(other -> other.isCrossListed(courseSections))
            .findFirst();

        if (crossListed.isPresent()) {
          // an exact match has been found, the sections are identical so simply add the
          // subject code to the cross listed course offering
          crossListed.get().addSubjectCode(subjectCode);
        }
        else {
          // no cross listed match found for this course so we create a new course offering
          Set<CourseOfferingSection> offeringSections = courseSections.stream()
              .map(o -> o.toCourseOfferingSection(termCode))
              .collect(Collectors.toSet());
          offerings.add(new CourseOffering(termCode, courseNumber, subjectCode, offeringSections));
        }

        // ignore these sections, as they have been registered in a course offering
        ignore.addAll(courseSections);
      }

      result.addAll(offerings);
    }

    return result;
  }

  private Collection<Section> getSections(String subjectCode) {
    return sections.get(subjectCode);
  }

  private Stream<Integer> courseNumbers() {
    return sections.values()
        .stream()
        .map(Section::getCourseNumber)
        .distinct();
  }

  private Stream<Section> sections(String subjectCode, int courseNumber) {
    return getSections(subjectCode).stream()
        .filter(section -> section.getCourseNumber() == courseNumber);
  }

  private Multimap<String, Section> sections(int courseNumber) {
    Multimap<String, Section> result = ArrayListMultimap.create();
    sections.values().stream()
        .filter(section -> section.getCourseNumber() == courseNumber)
        .forEach(section -> result.put(section.getSubjectCode(), section));
    return result;
  }

  private Optional<Section> sections(String subjectCode, int courseNumber, int sectionNumber) {
    return sections(subjectCode, courseNumber)
        .filter(section -> section.getSectionNumber() == sectionNumber)
        .findFirst();
  }

  public Section addSection(Section section) {
    Section existing = sections(section.getSubjectCode(), section.getCourseNumber(),
        section.getSectionNumber()).orElse(null);

    if (existing == null) {
      this.sections.put(section.getSubjectCode(), section);
      return section;
    }

    existing.combine(section);
    return existing;
  }

  public void addSections(Stream<DirEntry> dirEntries) {
    AtomicReference<String> subjectCode = new AtomicReference<>();

    dirEntries.forEach(dirEntry ->  {
      if (dirEntry instanceof SubjectDirEntry) {
        subjectCode.set(((SubjectDirEntry) dirEntry).getSubjectCode());
      }
      else if (dirEntry instanceof SectionDirEntry) {
        Section section = ((SectionDirEntry) dirEntry).toSection(subjectCode.get());
        addSection(section);
      }
    });
  }
}
