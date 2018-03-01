package com.keenant.madgrades.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.keenant.madgrades.entries.CourseNameEntry;
import com.keenant.madgrades.entries.DirEntry;
import com.keenant.madgrades.entries.GradesEntry;
import com.keenant.madgrades.entries.SectionEntry;
import com.keenant.madgrades.entries.SectionGradesEntry;
import com.keenant.madgrades.entries.SubjectAbbrevEntry;
import com.keenant.madgrades.entries.SubjectCodeEntry;
import com.keenant.madgrades.entries.SubjectNameEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Term {
  private final int termCode;

  /** subject code -> list of sections */
  private final Multimap<String, DirSection> sections = ArrayListMultimap
      .create(10, 1000);

  /** course number -> list of grades */
  private final Multimap<Integer, SectionGrades> grades = ArrayListMultimap.create();

  /** subject code -> course number -> course name */
  private final Table<String, Integer, String> courseNames = HashBasedTable.create();

  private final Map<String, String> subjectNames = new HashMap<>();
  private final Map<String, String> subjectAbbrevs = new HashMap<>();

  public Term(int termCode) {
    this.termCode = termCode;
  }

  public List<CourseOffering> generateCourseOfferings() {
    List<CourseOffering> result = new ArrayList<>();

    Map<String, Subject> subjects = new HashMap<>();
    Set<String> subjectCodes = Sets.union(subjectNames.keySet(), subjectAbbrevs.keySet());
    for (String subjectCode : subjectCodes) {
      String name = subjectNames.get(subjectCode);
      String abbrev = subjectAbbrevs.get(subjectCode);
      subjects.put(subjectCode, new Subject(name, abbrev, subjectCode));
    }

    Set<Integer> courseNumbers = courseNumbers().collect(Collectors.toSet());

    Set<DirSection> ignore = new HashSet<>();

    for (int courseNumber : courseNumbers) {
      Multimap<String, DirSection> sections = getSections(courseNumber);

      // list of course offerings for this course number
      // i.e. [CS 252/ECE 252, DS 252]
      List<CourseOffering> offerings = new ArrayList<>();

      for (String subjectCode : sections.keys()) {
        Set<DirSection> courseSections = sections(subjectCode, courseNumber).collect(Collectors.toSet());

        // ignore already added course sections
        if (ignore.containsAll(courseSections))
          continue;

        // find a course offering for this course number that has the exact same
        // set of sections
        Optional<CourseOffering> crossListed = offerings.stream()
            .filter(other -> other.isCrossListed(courseSections))
            .findFirst();

        List<SectionGrades> sectionGrades = grades.get(courseNumber).stream()
            .filter(s -> s.getSubjectCode().equals(subjectCode))
            .collect(Collectors.toList());

        Subject subject = subjects.get(subjectCode);

        if (crossListed.isPresent()) {
          // an exact match has been found, the sections are identical so simply add the
          // subject code to the cross listed course offering
          crossListed.get().addSubject(subject);
          crossListed.get().addGrades(sectionGrades);
        }
        else {
          // no cross listed match found for this course so we create a new course offering
          Set<Section> offeringSections = courseSections.stream()
              .map(o -> o.toSection(termCode))
              .collect(Collectors.toSet());

          String name = courseNames.get(subjectCode, courseNumber);

          CourseOffering offering = new CourseOffering(termCode, courseNumber, subject, name, offeringSections);
          offerings.add(offering);

          offering.addGrades(sectionGrades);
        }

        // ignore these sections, as they have been registered in a course offering
        ignore.addAll(courseSections);
      }

      result.addAll(offerings);
    }

    return result;
  }

  private Collection<DirSection> getSections(String subjectCode) {
    return sections.get(subjectCode);
  }

  private Stream<Integer> courseNumbers() {
    return sections.values()
        .stream()
        .map(DirSection::getCourseNumber)
        .distinct();
  }

  private Stream<DirSection> sections(String subjectCode, int courseNumber) {
    return getSections(subjectCode).stream()
        .filter(section -> section.getCourseNumber() == courseNumber);
  }

  /**
   * Get sections for a particular course number, organized by subject code -> sections.
   * @param courseNumber the course number to find
   * @return map of subject code -> list of sections
   */
  private Multimap<String, DirSection> getSections(int courseNumber) {
    Multimap<String, DirSection> result = ArrayListMultimap.create();
    sections.values().stream()
        .filter(section -> section.getCourseNumber() == courseNumber)
        .forEach(section -> result.put(section.getSubjectCode(), section));
    return result;
  }

  /**
   * Add this section to this term, or merge it with a matching section.
   * @param section the sectino to add
   */
  public void registerSection(DirSection section) {
    DirSection existing = sections(section.getSubjectCode(), section.getCourseNumber())
        .filter(other -> other.matches(section))
        .findFirst()
        .orElse(null);

    if (existing == null) {
      this.sections.put(section.getSubjectCode(), section);
      return;
    }

    existing.combineInstructors(section);
  }

  public void addSections(Stream<DirEntry> dirEntries) {
    // track subject code as it is streamed in
    AtomicReference<String> subjectCode = new AtomicReference<>();

    dirEntries.forEach(dirEntry ->  {
      if (dirEntry instanceof SubjectCodeEntry) {
        subjectCode.set(((SubjectCodeEntry) dirEntry).getSubjectCode());
      }
      else if (dirEntry instanceof SubjectNameEntry) {
        String subjectName = ((SubjectNameEntry) dirEntry).getSubjectName();
        subjectNames.put(subjectCode.get(), subjectName);
      }
      else if (dirEntry instanceof SectionEntry) {
        DirSection section = ((SectionEntry) dirEntry).toDirSection(subjectCode.get());
        registerSection(section);
      }
    });
  }

  public void addGrades(Stream<GradesEntry> gradesEntries) {
    // track subject
    AtomicReference<String> subjectCode = new AtomicReference<>();

    // track sections stream in
    // once we hit a course name we should process this list
    List<SectionGradesEntry> backlog = new ArrayList<>();

    gradesEntries.forEach(gradesEntry -> {
      if (gradesEntry instanceof SubjectCodeEntry) {
        subjectCode.set(((SubjectCodeEntry) gradesEntry).getSubjectCode());
      }
      else if (gradesEntry instanceof SubjectAbbrevEntry) {
        String abbrev = ((SubjectAbbrevEntry) gradesEntry).getSubjectAbbrev();
        subjectAbbrevs.put(subjectCode.get(), abbrev);
      }
      else if (gradesEntry instanceof SectionGradesEntry) {
        backlog.add((SectionGradesEntry) gradesEntry);
      }
      else if (gradesEntry instanceof CourseNameEntry) {
        String courseName = ((CourseNameEntry) gradesEntry).getCourseName();
        int courseNumber = -1;

        for (SectionGradesEntry entry : backlog) {
          courseNumber = entry.getCourseNumber();

          grades.put(entry.getCourseNumber(), new SectionGrades(
              subjectCode.get(),
              courseNumber,
              entry.getSectionNumber(),
              entry.getGradeDistribution()
          ));
        }

        courseNames.put(subjectCode.get(), courseNumber, courseName);

        backlog.clear();
      }
    });
  }
}
